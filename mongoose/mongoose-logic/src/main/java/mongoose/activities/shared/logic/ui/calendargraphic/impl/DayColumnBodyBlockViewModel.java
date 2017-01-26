package mongoose.activities.shared.logic.ui.calendargraphic.impl;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import mongoose.activities.shared.logic.calendar.CalendarTimeline;
import mongoose.activities.shared.logic.ui.calendargraphic.CalendarCell;
import mongoose.activities.shared.logic.ui.calendargraphic.CalendarClickEvent;
import mongoose.activities.shared.logic.ui.calendargraphic.CalendarGraphic;
import mongoose.activities.shared.logic.time.TimeInterval;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Translate;

/**
 * @author Bruno Salmon
 */
class DayColumnBodyBlockViewModel implements HorizontalDayPositioned, VerticalDayTimePositioned, CalendarCell {

    private final static Font textFont = Font.font("Verdana", 13);
    private final static Font timeFont = Font.font("Verdana", 10);
    private final static Paint timeFill = Color.WHITE;

    private long epochDay;
    private TimeInterval dayTimeMinuteInterval;
    private final Rectangle rectangle = new Rectangle();
    private final Text blockText = new Text();
    private Text startTimeText;
    private Text endTimeText;
    private final Group group = new Group();
    private final Translate translate = new Translate();

    {
        blockText.setFont(textFont);
        blockText.setTextAlignment(TextAlignment.CENTER);
        blockText.setFill(DayColumnHeaderViewModel.dayColumnHeaderTextColor);
        blockText.setTextOrigin(VPos.CENTER);
        blockText.setMouseTransparent(true);
        group.setAutoSizeChildren(false);
        group.getTransforms().setAll(translate);
    }

    DayColumnBodyBlockViewModel(CalendarGraphic calendarGraphic, long epochDay, TimeInterval dayTimeMinuteInterval, CalendarTimeline timeline, boolean displayTimes) {
        init(calendarGraphic, epochDay, dayTimeMinuteInterval, timeline, displayTimes);
    }

    void init(CalendarGraphic calendarGraphic, long epochDay, TimeInterval dayTimeMinuteInterval, CalendarTimeline timeline, boolean displayTimes) {
        this.epochDay = epochDay;
        this.dayTimeMinuteInterval = dayTimeMinuteInterval;
        Property<String> displayNameProperty = timeline.displayNameProperty();
        if (displayNameProperty != null)
            blockText.textProperty().bind(displayNameProperty);
        else {
            blockText.textProperty().unbind();
            blockText.setText(null);
        }
        rectangle.setFill(timeline.getBackgroundFill());
        rectangle.setOnMouseClicked(event -> {
            if (calendarGraphic.getCalendarClickHandler() != null)
                calendarGraphic.getCalendarClickHandler().handle(new CalendarClickEvent(event, this, timeline));
        });
        ObservableList<Node> children = group.getChildren();
        if (!displayTimes) {
            startTimeText = endTimeText = null;
            if (children.size() != 2)
                children.setAll(rectangle, blockText);
        } else {
            if (startTimeText == null) {
                startTimeText = new Text();
                startTimeText.setFont(timeFont);
                startTimeText.setTextAlignment(TextAlignment.LEFT);
                startTimeText.setFill(timeFill);
                startTimeText.setTextOrigin(VPos.TOP);
                startTimeText.setY(1d);
                startTimeText.setMouseTransparent(true);
            }
            startTimeText.setText(dayTimeMinuteInterval.getStartText());
            if (endTimeText == null) {
                endTimeText = new Text();
                endTimeText.setFont(timeFont);
                endTimeText.setTextAlignment(TextAlignment.LEFT);
                endTimeText.setFill(timeFill);
                endTimeText.setTextOrigin(VPos.BOTTOM);
                endTimeText.setMouseTransparent(true);
            }
            endTimeText.setText(dayTimeMinuteInterval.getEndText());
            if (children.size() != 4)
                children.setAll(rectangle, blockText, startTimeText, endTimeText);
        }
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public long getEpochDay() {
        return epochDay;
    }

    @Override
    public TimeInterval getDayTimeMinuteInterval() {
        return dayTimeMinuteInterval;
    }

    @Override
    public void setXAndWidth(double x, double width) {
        translate.setX(x);
        rectangle.setWidth(width);
        blockText.setWrappingWidth(width);
        if (startTimeText != null) {
            startTimeText.setWrappingWidth(width - 2);
            endTimeText.setWrappingWidth(width - 2);
        }
    }

    @Override
    public void setYAndHeight(double y, double height) {
        translate.setY(y);
        rectangle.setHeight(height);
        blockText.setY(height / 2);
        if (endTimeText != null)
            endTimeText.setY(height - 1d);
    }
}
