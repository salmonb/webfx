package webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.markers;

import javafx.beans.property.Property;
import javafx.scene.Parent;

/**
 * @author Bruno Salmon
 */
public interface HasRootProperty {

    Property<Parent> rootProperty();
    default void setRoot(Parent root) { rootProperty().setValue(root); }
    default Parent getRoot() { return rootProperty().getValue(); }
}