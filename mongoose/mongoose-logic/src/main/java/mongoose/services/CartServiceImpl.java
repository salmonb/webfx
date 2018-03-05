package mongoose.services;

import mongoose.activities.shared.logic.work.WorkingDocument;
import mongoose.activities.shared.logic.work.WorkingDocumentLine;
import mongoose.activities.shared.logic.work.sync.WorkingDocumentLoader;
import mongoose.entities.*;
import naga.framework.orm.domainmodel.DataSourceModel;
import naga.framework.orm.entity.EntityId;
import naga.framework.orm.entity.EntityList;
import naga.framework.orm.entity.EntityStore;
import naga.framework.orm.entity.EntityStoreQuery;
import naga.platform.services.log.spi.Logger;
import naga.util.Strings;
import naga.util.async.Future;
import naga.util.collection.Collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
class CartServiceImpl implements CartService {

    private final static Map<Object, CartService> services = new HashMap<>();

    private final EntityStore store;
    private Object id;
    private String uuid;
    private Cart cart;
    private List<Document> cartDocuments;
    private List<WorkingDocument> cartWorkingDocuments;
    private EntityList<MoneyTransfer> cartPayments;
    private EventService eventService;
    private boolean loading;

    public CartServiceImpl(Object cartIdOrUuid, EntityStore store) {
        id = cartIdOrUuid instanceof String ? null : cartIdOrUuid;
        uuid = cartIdOrUuid instanceof String ? (String) cartIdOrUuid : null;
        this.store = store;
    }

    static CartService get(Object cartIdOrUuid) {
        return services.get(toKey(cartIdOrUuid));
    }

    static CartService getOrCreate(Object cartIdOrUuid, EntityStore store) {
        cartIdOrUuid = toKey(cartIdOrUuid);
        CartService cartService = get(cartIdOrUuid);
        if (cartService == null)
            services.put(cartIdOrUuid, cartService = new CartServiceImpl(cartIdOrUuid, store));
        return cartService;
    }

    static CartService getOrCreate(Object cartIdOrUuid, DataSourceModel dataSourceModel) {
        return getOrCreate(cartIdOrUuid, EntityStore.create(dataSourceModel));
    }

    static CartService getOrCreateFromCart(Cart cart) {
        CartService service = getOrCreate(cart.getId(), cart.getStore());
        ((CartServiceImpl) service).setCart(cart);
        return service;
    }

    static CartService getOrCreateFromDocument(Document document) {
        return getOrCreateFromCart(document.getCart());
    }

    private static Object toKey(Object id) {
        if (id instanceof EntityId)
            id = ((EntityId) id).getPrimaryKey();
        return id;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
        if (id == null)
            services.put(id = toKey(cart.getId()), this);
        if (uuid == null)
            services.put(uuid = cart.getUuid(), this);
        if (eventService != null)
            eventService.setCurrentCart(cart);
    }

    @Override
    public Cart getCart() {
        return cart;
    }

    @Override
    public List<Document> getCartDocuments() {
        return cartDocuments;
    }

    @Override
    public List<WorkingDocument> getCartWorkingDocuments() {
        return cartWorkingDocuments;
    }

    @Override
    public EntityList<MoneyTransfer> getCartPayments() {
        return cartPayments;
    }

    @Override
    public void unload() {
        cartDocuments = null;
        cartWorkingDocuments = null;
        cartPayments = null;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean isLoaded() {
        return cartDocuments != null && !loading;
    }

    @Override
    public Future<Cart> onCart() {
        if (isLoaded())
            return Future.succeededFuture(cart);
        String documentCondition = "document.cart." + (id != null ? "id=?" : "uuid=?");
        Object[] parameter = new Object[]{id != null ? id : uuid};
        return store.executeQueryBatch(
              new EntityStoreQuery(Strings.replaceAll(WorkingDocumentLoader.DOCUMENT_LINE_LOAD_QUERY, "document=?", documentCondition), parameter)
            , new EntityStoreQuery(Strings.replaceAll(WorkingDocumentLoader.ATTENDANCE_LOAD_QUERY, "document=?", documentCondition), parameter)
            , new EntityStoreQuery(Strings.replaceAll(WorkingDocumentLoader.PAYMENT_LOAD_QUERY, "document=?", documentCondition), parameter)
        ).compose((entityLists, future) -> {
            EntityList<DocumentLine> dls = entityLists[0];
            EntityList<Attendance> as = entityLists[1];
            cartPayments = entityLists[2];
            cartDocuments = new ArrayList<>();
            cartWorkingDocuments = new ArrayList<>();
            if (dls.isEmpty()) {
                loading = false;
                future.complete();
            }
            eventService = EventService.getOrCreateFromDocument(dls.get(0).getDocument());
            eventService.onEventOptions().setHandler(ar -> {
                if (!cartDocuments.isEmpty()) {
                    Logger.log("Warning: CartService.onCart() has been called again before the first call is finished");
                    cartDocuments.clear();
                    cartWorkingDocuments.clear();
                }
                Document currentDocument = null;
                List<WorkingDocumentLine> wdls = null;
                for (DocumentLine dl : dls) {
                    Document document = dl.getDocument();
                    if (document != currentDocument) {
                        if (currentDocument != null)
                            addWorkingDocument(currentDocument, wdls);
                        cartDocuments.add(currentDocument = document);
                        wdls = new ArrayList<>();
                    }
                    wdls.add(new WorkingDocumentLine(dl, Collections.filter(as, a -> a.getDocumentLine() == dl), eventService));
                }
                addWorkingDocument(currentDocument, wdls);
                setCart(cartDocuments.get(0).getCart());
                loading = false;
                future.complete(cart);
            });
        });
    }

    private void addWorkingDocument(Document document, List<WorkingDocumentLine> wdls) {
        cartWorkingDocuments.add(new WorkingDocument(new WorkingDocument(eventService, document, wdls)));
    }

    @Override
    public Future<List<Document>> onCartDocuments() {
        return onCart().map(cart -> cartDocuments);
    }

    @Override
    public Future<List<WorkingDocument>> onCartWorkingDocuments() {
        return onCart().map(cart -> cartWorkingDocuments);
    }

    @Override
    public Future<EntityList> onCartPayments() {
        return onCart().map(cart -> cartPayments);
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }
}
