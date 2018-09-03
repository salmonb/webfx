package webfx.fxkits.extra.spi.peer.base;

import emul.javafx.scene.Node;
import webfx.fxkits.extra.control.DataGrid;
import webfx.fxkits.extra.displaydata.DisplayColumn;

/**
 * @author Bruno Salmon
 */
public interface DataGridPeerImageTextMixin
        <C, N extends DataGrid, NB extends DataGridPeerBase<C, N, NB, NM>, NM extends DataGridPeerMixin<C, N, NB, NM>>

        extends DataGridPeerMixin<C, N, NB, NM> {

    default void setCellContent(C cell, Node content, DisplayColumn displayColumn) {
        setCellImageAndTextContent(cell, content, null, displayColumn);
    }

    default void setCellTextContent(C cell, String text, DisplayColumn displayColumn) {
        setCellImageAndTextContent(cell, null, text, displayColumn);
    }

    void setCellImageAndTextContent(C cell, Node image, String text, DisplayColumn displayColumn);

}