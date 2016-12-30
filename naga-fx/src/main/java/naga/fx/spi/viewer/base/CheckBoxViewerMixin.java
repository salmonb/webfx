package naga.fx.spi.viewer.base;

import naga.fx.scene.control.CheckBox;

/**
 * @author Bruno Salmon
 */
public interface CheckBoxViewerMixin
        <N extends CheckBox, NB extends CheckBoxViewerBase<N, NB, NM>, NM extends CheckBoxViewerMixin<N, NB, NM>>

        extends ButtonBaseViewerMixin<N, NB, NM> {

    void updateSelected(Boolean selected);
}