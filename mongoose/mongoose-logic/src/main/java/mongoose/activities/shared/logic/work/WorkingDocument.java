package mongoose.activities.shared.logic.work;

import mongoose.activities.shared.logic.price.DocumentPricing;
import mongoose.activities.shared.logic.time.DateTimeRange;
import mongoose.activities.shared.logic.time.DaysArray;
import mongoose.activities.shared.logic.time.DaysArrayBuilder;
import mongoose.activities.shared.logic.time.TimeInterval;
import mongoose.entities.*;
import mongoose.entities.markers.EntityHasPersonDetails;
import mongoose.entities.markers.HasPersonDetails;
import mongoose.services.EventService;
import naga.commons.util.async.Batch;
import naga.commons.util.async.Future;
import naga.commons.util.collection.Collections;
import naga.commons.util.function.Predicate;
import naga.framework.expression.sqlcompiler.sql.SqlCompiled;
import naga.framework.orm.domainmodel.DataSourceModel;
import naga.framework.orm.domainmodel.DomainModel;
import naga.framework.orm.entity.Entity;
import naga.framework.orm.entity.EntityList;
import naga.framework.orm.entity.EntityStore;
import naga.framework.orm.entity.UpdateStore;
import naga.framework.orm.mapping.QueryResultSetToEntityListGenerator;
import naga.platform.services.query.QueryArgument;
import naga.platform.services.query.QueryResultSet;
import naga.platform.services.update.UpdateArgument;
import naga.platform.spi.Platform;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Bruno Salmon
 */
public class WorkingDocument {

    private final EventService eventService;
    private final Document document;
    private final List<WorkingDocumentLine> workingDocumentLines;
    private Integer computedPrice;
    private UpdateStore updateStore;
    private WorkingDocument loadedWorkingDocument;

    public WorkingDocument(EventService eventService, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, eventService.getPersonService().getPreselectionProfilePerson(), workingDocumentLines);
    }

    public WorkingDocument(EventService eventService, WorkingDocument wd, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, createDocument(wd.getDocument()), workingDocumentLines);
        loadedWorkingDocument = wd.loadedWorkingDocument;
    }

    public WorkingDocument(EventService eventService, Person person, List<WorkingDocumentLine> workingDocumentLines) {
        this(eventService, createDocument(person), workingDocumentLines);
    }

    public WorkingDocument(EventService eventService, Document document, List<WorkingDocumentLine> workingDocumentLines) {
        this.eventService = eventService;
        this.document = document;
        this.workingDocumentLines = workingDocumentLines;
        Collections.forEach(workingDocumentLines, wdl -> wdl.setWorkingDocument(this));
    }

    // Constructor used to make a copy (that can be changed) of a loaded working document (that shouldn't be changed).
    // When submitting this copy, some decisions are made by comparison with the original loaded document.
    public WorkingDocument(WorkingDocument loadedWorkingDocument) {
        this(loadedWorkingDocument.getEventService(), createDocument(loadedWorkingDocument.getDocument()), new ArrayList<>(loadedWorkingDocument.getWorkingDocumentLines()));
        this.loadedWorkingDocument = loadedWorkingDocument;
    }

    public EventService getEventService() {
        return eventService;
    }

    public WorkingDocument getLoadedWorkingDocument() {
        return loadedWorkingDocument;
    }

    public Document getDocument() {
        return document;
    }

    public List<WorkingDocumentLine> getWorkingDocumentLines() {
        return workingDocumentLines;
    }

    private DateTimeRange dateTimeRange;

    public DateTimeRange getDateTimeRange() {
        if (dateTimeRange == null) {
            long includedStart = Long.MAX_VALUE, excludedEnd = Long.MIN_VALUE;
            for (WorkingDocumentLine wdl : getWorkingDocumentLines()) {
                if (isWorkingDocumentLineToBeIncludedInWorkingDocumentDateTimeRange(wdl)) {
                    DateTimeRange wdlDateTimeRange = wdl.getDateTimeRange();
                    if (wdlDateTimeRange != null && !wdlDateTimeRange.isEmpty()) {
                        TimeInterval interval = wdlDateTimeRange.getInterval().changeTimeUnit(TimeUnit.MINUTES);
                        includedStart = Math.min(includedStart, interval.getIncludedStart());
                        excludedEnd = Math.max(excludedEnd, interval.getExcludedEnd());
                    }
                }
            }
            dateTimeRange = new DateTimeRange(new TimeInterval(includedStart, excludedEnd, TimeUnit.MINUTES));
        }
        return dateTimeRange;
    }

    private boolean isWorkingDocumentLineToBeIncludedInWorkingDocumentDateTimeRange(WorkingDocumentLine wdl) {
        return wdl.getDayTimeRange() != null; // Excluding lines with no day time range (ex: diet option)
    }

    public WorkingDocument applyBusinessRules() {
        applyBreakfastRule();
        applyDietRule();
        return this;
    }

    private void applyBreakfastRule() {
        if (!hasAccommodation())
            workingDocumentLines.remove(getBreakfastLine());
        else if (!hasBreakfast()) {
            Option breakfastOption = eventService.getBreakfastOption();
            if (breakfastOption != null)
                breakfastLine = addNewDependantLine(breakfastOption, getAccommodationLine(), 1);
        }
    }

    private void applyDietRule() {
        if (!hasLunch() && !hasSupper()) {
            if (hasDiet())
                workingDocumentLines.remove(getDietLine());
        } else {
            WorkingDocumentLine dietLine = getDietLine();
            if (dietLine == null) {
                Option dietOption = eventService.getDietOption();
                if (dietOption == null)
                    return;
                dietLine = new WorkingDocumentLine(dietOption, this);
                workingDocumentLines.add(dietLine);
            }
            DaysArrayBuilder dab = new DaysArrayBuilder();
            if (hasLunch())
                dab.addSeries(getLunchLine().getDaysArray().toSeries(), null);
            if (hasSupper())
                dab.addSeries(getSupperLine().getDaysArray().toSeries(), null);
            dietLine.setDaysArray(dab.build());
        }
    }

    public void clearLinesCache() {
        accommodationLine = breakfastLine = lunchLine = supperLine = dietLine = null;
        clearComputedPrice();
    }

    public void clearComputedPrice() {
        computedPrice = null;
    }

    public int getComputedPrice() {
        if (computedPrice == null)
            computedPrice = computePrice();
        return computedPrice;
    }

    public int computePrice() {
        return computedPrice = DocumentPricing.computeDocumentPrice(this);
    }

    //// Accommodation line

    private WorkingDocumentLine accommodationLine;

    public WorkingDocumentLine getAccommodationLine() {
        if (accommodationLine == null)
            accommodationLine = findLine(WorkingDocumentLine::isAccommodation);
        return accommodationLine;
    }

    private WorkingDocumentLine findLine(Predicate<WorkingDocumentLine> predicate) {
        return Collections.findFirst(workingDocumentLines, predicate);
    }

    private boolean hasAccommodation() {
        return getAccommodationLine() != null;
    }

    //// Breakfast line

    private WorkingDocumentLine breakfastLine;

    private WorkingDocumentLine getBreakfastLine() {
        if (breakfastLine == null)
            breakfastLine = Collections.findFirst(workingDocumentLines, wdl -> wdl.getOption().isBreakfast());
        return breakfastLine;
    }

    private boolean hasBreakfast() {
        return getBreakfastLine() != null;
    }

    //// Lunch line

    private WorkingDocumentLine lunchLine;

    private WorkingDocumentLine getLunchLine() {
        if (lunchLine == null)
            lunchLine = findLine(wdl -> wdl.getOption().isLunch());
        return lunchLine;
    }

    private boolean hasLunch() {
        return getLunchLine() != null;
    }

    //// Supper line

    private WorkingDocumentLine supperLine;

    private WorkingDocumentLine getSupperLine() {
        if (supperLine == null)
            supperLine = findLine(wdl -> wdl.getOption().isSupper());
        return supperLine;
    }

    private boolean hasSupper() {
        return getSupperLine() != null;
    }


    //// Diet line

    private WorkingDocumentLine dietLine;

    private WorkingDocumentLine getDietLine() {
        if (dietLine == null)
            dietLine = findLine(WorkingDocumentLine::isDiet);
        return dietLine;
    }

    private boolean hasDiet() {
        return getDietLine() != null;
    }

    private WorkingDocumentLine addNewDependantLine(Option dependantOption, WorkingDocumentLine masterLine, long shiftDays) {
        WorkingDocumentLine dependantLine = new WorkingDocumentLine(dependantOption, this);
        workingDocumentLines.add(dependantLine);
        applySameAttendances(dependantLine, masterLine, shiftDays);
        return dependantLine;
    }

    private void applySameAttendances(WorkingDocumentLine dependantLine, WorkingDocumentLine masterLine, long shiftDays) {
        dependantLine.setDaysArray(masterLine.getDaysArray().shift(shiftDays));
    }

    public void syncPersonDetails(HasPersonDetails p) {
        syncPersonDetails(p, document);
    }

    private UpdateStore getUpdateStore() {
        if (updateStore == null)
            updateStore = getUpdateStore(document);
        return updateStore;
    }

    private static UpdateStore getUpdateStore(Entity entity) {
        return getUpdateStore(entity.getStore());
    }

    private static UpdateStore getUpdateStore(EntityStore store) {
        return store instanceof UpdateStore ? (UpdateStore) store : UpdateStore.createAbove(store);
    }

    private static Document createDocument(EntityHasPersonDetails personDetailsEntity) {
        UpdateStore store = getUpdateStore(personDetailsEntity);
        Document document;
        if (personDetailsEntity instanceof Document) // Keeping the same id if created from an original document
            document = store.createEntity(personDetailsEntity.getId());
        else // otherwise creating a new document
            document = store.createEntity(Document.class);
        syncPersonDetails(personDetailsEntity, document);
        return document;
    }

    public WorkingDocument mergeWithCalendarWorkingDocument(WorkingDocument calendarWorkingDocument, DateTimeRange dateTimeRange) {
        List<WorkingDocumentLine> lines = new ArrayList<>(calendarWorkingDocument.getWorkingDocumentLines());
        for (WorkingDocumentLine thisLine : getWorkingDocumentLines()) {
            WorkingDocumentLine line = calendarWorkingDocument.findSameWorkingDocumentLine(thisLine);
            if (line == null)
                lines.add(line = new WorkingDocumentLine(thisLine, dateTimeRange));
            line.syncInfoFrom(thisLine);
        }
        return new WorkingDocument(eventService, this, lines).applyBusinessRules();
    }

    private static void syncPersonDetails(HasPersonDetails p1, HasPersonDetails p2) {
        p2.setFirstName(p1.getFirstName());
        p2.setLastName(p1.getLastName());
        p2.setMale(p1.isMale());
        p2.setAge(p1.getAge());
        p2.setCarer1Name(p1.getCarer1Name());
        p2.setCarer2Name(p1.getCarer2Name());
        p2.setEmail(p1.getEmail());
        p2.setPhone(p1.getPhone());
        p2.setStreet(p1.getStreet());
        p2.setPostCode(p1.getPostCode());
        p2.setCityName(p1.getCityName());
        p2.setCountryName(p1.getCountryName());
        p2.setCountry(p1.getCountry());
        p2.setOrganization(p1.getOrganization());
        p2.setUnemployed(p1.isUnemployed());
        p2.setFacilityFee(p1.isFacilityFee());
        p2.setWorkingVisit(p1.isWorkingVisit());
        p2.setDiscovery(p1.isDiscovery());
        p2.setDiscoveryReduced(p1.isDiscoveryReduced());
        p2.setGuest(p1.isGuest());
        p2.setResident(p1.isResident());
        p2.setResident2(p1.isResident2());
    }

    private WorkingDocumentLine findSameWorkingDocumentLine(WorkingDocumentLine wdl) {
        for (WorkingDocumentLine thisWdl : getWorkingDocumentLines()) {
            if (sameLine(thisWdl, wdl))
                return thisWdl;
        }
        return null;
    }

    private static boolean sameLine(WorkingDocumentLine wdl1, WorkingDocumentLine wdl2) {
        return wdl1 == wdl2 || wdl1 != null && Entity.sameId(wdl1.getSite(), wdl2.getSite()) && Entity.sameId(wdl1.getItem(), wdl2.getItem());
    }

    private static String generateRandomUuid() {
        // return UUID.randomUUID().toString(); // Doesn't compile with GWT
        // From https://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript :
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
    }

    private static String s4() {
        return Integer.toHexString((int) Math.floor((1 + Math.random()) * 0x10000)).substring(1);
    }

    public Future<Document> submit(String comment) {
        UpdateStore store = getUpdateStore();
        Document du;
        if (loadedWorkingDocument != null)
            du = store.updateEntity(loadedWorkingDocument.getDocument());
        else {
            du = store.insertEntity(Document.class);
            du.setEvent(eventService.getEvent());
            Cart cart = eventService.getCurrentCart();
            if (cart == null) {
                cart = store.insertEntity(Cart.class);
                cart.setUuid(generateRandomUuid());
            }
            du.setCart(cart);
        }
        syncPersonDetails(document, du);
        for (WorkingDocumentLine wdl : workingDocumentLines) {
            DocumentLine dl = wdl.getDocumentLine(), dlu;
            if (dl == null) {
                dlu = store.insertEntity(DocumentLine.class);
                dlu.setDocument(du);
            } else {
                dlu = store.updateEntity(dl);
            }
            dlu.setSite(wdl.getSite());
            dlu.setItem(wdl.getItem());

            DaysArray daysArray = wdl.getDaysArray();
            List<Attendance> attendances = wdl.getAttendances();
            int j = 0, m = Collections.size(attendances), n = daysArray.getArray().length;
            if (m > 0 && n == 0) // means that all attendances have been removed
                removeLine(dl);
            else {
                for (int i = 0; i < n; i++) {
                    LocalDate date = daysArray.getDate(i);
                    while (j < m && attendances.get(j).getDate().compareTo(date) < 0) // isBefore() doesn't work on Android
                        store.deleteEntity(attendances.get(j++));
                    if (j < m && attendances.get(j).getDate().equals(date))
                        j++;
                    else {
                        Attendance au = store.insertEntity(Attendance.class);
                        au.setDate(date);
                        au.setDocumentLine(dlu);
                    }
                }
                while (j < m)
                    store.deleteEntity(attendances.get(j++));
            }
        }
        if (loadedWorkingDocument != null)
            for (WorkingDocumentLine lastWdl : loadedWorkingDocument.getWorkingDocumentLines()) {
                if (findSameWorkingDocumentLine(lastWdl) == null)
                    removeLine(lastWdl.getDocumentLine());
            }
        return store.executeUpdate(new UpdateArgument[]{new UpdateArgument("select set_transaction_parameters(false)", null, false, eventService.getEventDataSourceModel().getId())})
                .map(batch -> du);
    }

    private void removeLine(DocumentLine dl) {
        getUpdateStore().deleteEntity(dl); // TODO: should probably be cancelled instead in some cases (and keep the non refundable part)
    }

    public static Future<WorkingDocument> load(Document document) {
        return load(EventService.getOrCreateFromDocument(document), document.getPrimaryKey());
    }

    public final static String DOCUMENT_LINE_LOAD_QUERY = "select <frontend_cart>,document.<frontend_cart> from DocumentLine where site!=null and document=? order by document desc";
    public final static String ATTENDANCE_LOAD_QUERY = "select documentLine.id,date from Attendance where documentLine.document=? order by date";
    public final static String PAYMENT_LOAD_QUERY = "select <frontend_cart> from MoneyTransfer where document=? order by date";

    public static Future<WorkingDocument> load(EventService eventService, Object documentPk) {
        DataSourceModel dataSourceModel = eventService.getEventDataSourceModel();
        Object dataSourceId = dataSourceModel.getId();
        DomainModel domainModel = dataSourceModel.getDomainModel();
        SqlCompiled sqlCompiled1 = domainModel.compileSelect(DOCUMENT_LINE_LOAD_QUERY);
        SqlCompiled sqlCompiled2 = domainModel.compileSelect(ATTENDANCE_LOAD_QUERY);
        Object[] documentPkParameter = {documentPk};
        Future<Batch<QueryResultSet>> queryBatchFuture;
        return Future.allOf(eventService.onEventOptions(), queryBatchFuture = Platform.getQueryService().executeQueryBatch(
                new Batch<>(new QueryArgument[]{
                        new QueryArgument(sqlCompiled1.getSql(), documentPkParameter, dataSourceId),
                        new QueryArgument(sqlCompiled2.getSql(), documentPkParameter, dataSourceId)
                })
        )).compose(v -> {
            Batch<QueryResultSet> b = queryBatchFuture.result();
            EntityStore store = EntityStore.createAbove(eventService.getEventStore());
            EntityList<DocumentLine> dls = QueryResultSetToEntityListGenerator.createEntityList(b.getArray()[0], sqlCompiled1.getQueryMapping(), store, "dl");
            EntityList<Attendance> as = QueryResultSetToEntityListGenerator.createEntityList(b.getArray()[1], sqlCompiled2.getQueryMapping(), store, "a");
            List<WorkingDocumentLine> wdls = new ArrayList<>();
            for (DocumentLine dl : dls)
                wdls.add(new WorkingDocumentLine(dl, Collections.filter(as, a -> a.getDocumentLine() == dl), eventService));
            WorkingDocument loadedWorkingDocument = new WorkingDocument(eventService, store.getEntity(Document.class, documentPk), wdls);
            return Future.succeededFuture(new WorkingDocument(loadedWorkingDocument));
        });
    }
}
