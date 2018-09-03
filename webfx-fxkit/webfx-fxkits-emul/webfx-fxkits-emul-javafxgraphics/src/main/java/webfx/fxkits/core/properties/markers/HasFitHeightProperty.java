package webfx.fxkits.core.properties.markers;

import emul.javafx.beans.property.Property;

/**
 * @author Bruno Salmon
 */
public interface HasFitHeightProperty {

    Property<Double> fitHeightProperty();
    default void setFitHeight(Double fitHeight) { fitHeightProperty().setValue(fitHeight); }
    default Double getFitHeight() { return fitHeightProperty().getValue(); }

}