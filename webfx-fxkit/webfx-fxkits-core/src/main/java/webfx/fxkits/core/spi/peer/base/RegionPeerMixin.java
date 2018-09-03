package webfx.fxkits.core.spi.peer.base;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;

/**
 * @author Bruno Salmon
 */
public interface RegionPeerMixin
        <N extends Region, NB extends RegionPeerBase<N, NB, NM>, NM extends RegionPeerMixin<N, NB, NM>>

        extends NodePeerMixin<N, NB, NM> {

    void updateWidth(Double width);

    void updateHeight(Double height);

    void updateBackground(Background background);

    void updateBorder(Border border);

    void updatePadding(Insets padding);

}