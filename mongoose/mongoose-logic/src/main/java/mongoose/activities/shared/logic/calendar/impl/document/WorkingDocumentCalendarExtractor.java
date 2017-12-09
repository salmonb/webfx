package mongoose.activities.shared.logic.calendar.impl.document;

import javafx.beans.property.Property;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import mongoose.activities.shared.logic.calendar.Calendar;
import mongoose.activities.shared.logic.calendar.CalendarExtractor;
import mongoose.activities.shared.logic.calendar.CalendarTimeline;
import mongoose.activities.shared.logic.calendar.impl.CalendarImpl;
import mongoose.activities.shared.logic.calendar.impl.CalendarTimelineImpl;
import mongoose.activities.shared.logic.time.DateTimeRange;
import mongoose.activities.shared.logic.time.DayTimeRange;
import mongoose.activities.shared.logic.work.WorkingDocument;
import mongoose.activities.shared.logic.work.WorkingDocumentLine;
import mongoose.activities.shared.logic.work.business.logic.OptionLogic;
import mongoose.entities.ItemFamilyType;
import mongoose.entities.Label;
import mongoose.entities.Option;
import mongoose.entities.markers.HasItemFamilyType;
import mongoose.util.Labels;
import naga.framework.ui.i18n.I18n;
import naga.util.Objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Bruno Salmon
 */
public class WorkingDocumentCalendarExtractor implements CalendarExtractor<WorkingDocument> {

    private final static WorkingDocumentCalendarExtractor SINGLETON = new WorkingDocumentCalendarExtractor();

    private final static Paint TEACHING_FILL = Color.web("0xF5A463");
    private final static Paint ACCOMMODATION_FILL = Color.web("0x484A61");
    private final static Paint MEALS_FILL = Color.web("0xA44F5F");
    private final static Paint TRANSPORT_FILL = Color.web("0x8CA76A");
    private final static Paint UNATTENDED_FILL = Color.DARKGRAY;
    private final static Paint NOTHING_FILL = Color.LIGHTGRAY;

    public static WorkingDocumentCalendarExtractor get() {
        return SINGLETON;
    }

    public static Paint getItemFamilyFillColor(HasItemFamilyType hasItemFamilyType) {
        return getItemFamilyFillColor(hasItemFamilyType.getItemFamilyType());
    }

    public static Paint getItemFamilyFillColor(ItemFamilyType itemFamilyType) {
        switch (itemFamilyType) {
            case TEACHING: return TEACHING_FILL;
            case ACCOMMODATION: return ACCOMMODATION_FILL;
            case MEALS: return MEALS_FILL;
            case TRANSPORT: return TRANSPORT_FILL;
        }
        return null;
    }

    private WorkingDocumentCalendarExtractor() {
    }

    private static class OptionTimeline {
        final Option option;
        WorkingDocumentLine workingDocumentLine;
        WorkingDocumentLine maxWorkingDocumentLine;

        OptionTimeline(Option option, WorkingDocumentLine workingDocumentLine, boolean isMax) {
            this.option = option;
            setWorkingDocumentLine(workingDocumentLine, isMax);
        }

        void setWorkingDocumentLine(WorkingDocumentLine wdl, boolean isMax) {
            if (isMax)
                maxWorkingDocumentLine = wdl;
            else
                workingDocumentLine = wdl;
        }

        void addToCalendarTimelines(Collection<CalendarTimeline> timelines, I18n i18n, DateTimeRange calendarDateTimeRange) {
            DayTimeRange dayTimeRange = option.getParsedTimeRangeOrParent();
            //timelines.add(new CalendarTimelineImpl(calendarDateTimeRange, dayTimeRange, null, NOTHING_FILL));
            Label label = Labels.bestLabelOrName(!option.isAccommodation() ? option : option.getParent() /* normally: night */);
            Property<String> displayNameProperty = Labels.translateLabel(label, i18n);
            if (maxWorkingDocumentLine != null) {
                DateTimeRange dateTimeRange = new DateTimeRange(maxWorkingDocumentLine.getDaysArray());
                if (!dateTimeRange.isEmpty()) {
                    Paint fill = UNATTENDED_FILL;
                    timelines.add(new CalendarTimelineImpl(dateTimeRange, dayTimeRange, displayNameProperty, fill, maxWorkingDocumentLine));
                }
            }
            if (workingDocumentLine != null) {
                DateTimeRange dateTimeRange = new DateTimeRange(workingDocumentLine.getDaysArray());
                if (!dateTimeRange.isEmpty()) {
                    Paint fill = Objects.coalesce(getItemFamilyFillColor(option), UNATTENDED_FILL);
                    timelines.add(new CalendarTimelineImpl(dateTimeRange, dayTimeRange, displayNameProperty, fill, workingDocumentLine));
                }
            }
        }
    }

    @Override
    public Calendar extractCalendar(WorkingDocument wd, I18n i18n) {
        return extractCalendar(wd, null, i18n);
    }

    public Calendar extractCalendar(WorkingDocument wd, WorkingDocument maxWd, I18n i18n) {
        Map<Object, OptionTimeline> optionTimelines = new HashMap<>();
        // Gathering options coming from document lines
        addWorkingDocumentIntoOptionTimelines(wd, false, optionTimelines);
        // And those coming from options preselection
        addWorkingDocumentIntoOptionTimelines(maxWd, true, optionTimelines);
        // Generating calendar timelines from option timelines
        Collection<CalendarTimeline> timelines = new ArrayList<>();
        DateTimeRange calendarDateTimeRange = Objects.coalesce(maxWd, wd).getDateTimeRange().changeTimeUnit(TimeUnit.DAYS);
        for (OptionTimeline ot : optionTimelines.values())
            timelines.add(new CalendarTimelineImpl(calendarDateTimeRange, ot.option.getParsedTimeRangeOrParent(), null, NOTHING_FILL, ot.option));
        for (OptionTimeline ot : optionTimelines.values())
            ot.addToCalendarTimelines(timelines, i18n, calendarDateTimeRange);
        return new CalendarImpl(calendarDateTimeRange.getInterval(), timelines);
    }

    private void addWorkingDocumentIntoOptionTimelines(WorkingDocument wd, boolean isMax, Map<Object, OptionTimeline> optionTimelines) {
        if (wd != null)
            for (WorkingDocumentLine wdl : wd.getWorkingDocumentLines()) {
                Option option = wdl.getOption();
                if (OptionLogic.isOptionDisplayableOnCalendar(option, isMax)) {
                    OptionTimeline optionTimeline = optionTimelines.get(option.getPrimaryKey());
                    if (optionTimeline != null)
                        optionTimeline.setWorkingDocumentLine(wdl, isMax);
                    else
                        optionTimelines.put(option.getPrimaryKey(), new OptionTimeline(option, wdl, isMax));
                }
            }
    }

}
