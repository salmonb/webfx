package webfx.fxkits.extra.cell.rowstyle;

import webfx.platforms.core.util.Objects;
import webfx.platforms.core.util.Strings;
import webfx.platforms.core.util.function.Function;
import javafx.scene.paint.Paint;

/**
 * @author Bruno Salmon
 */
public class RowStyleUpdater {
    private final RowAdapter row;
    private final Function<Integer, Object[]> rowStyleClassesGetter;
    private final Function<Integer, Paint> rowFillGetter;
    private Object[] styles;

    public RowStyleUpdater(RowAdapter row, Function<Integer, Object[]> rowStyleClassesGetter, Function<Integer, Paint> rowFillGetter) {
        this.row = row;
        this.rowStyleClassesGetter = rowStyleClassesGetter;
        this.rowFillGetter = rowFillGetter;
    }

    public void update() {
        if (rowStyleClassesGetter != null)
            updateRowStyle(rowStyleClassesGetter.apply(row.getRowIndex()));
        if (rowFillGetter != null)
            updateRowFill(rowFillGetter.apply(row.getRowIndex()));
    }

    private void updateRowStyle(Object[] newStyles) {
        if (newStyles != null)
            for (int i = 0; i < newStyles.length; i++) {
                String newStyleClass = Strings.toString(newStyles[i]);
                String oldStyleClass = styles == null ? null : Strings.toString(styles[i]);
                if (!Objects.areEquals(newStyleClass, oldStyleClass)) {
                    if (oldStyleClass != null)
                        row.removeStyleClass(oldStyleClass);
                    if (newStyleClass != null)
                        row.addStyleClass(newStyleClass);
                }
            }
        styles = newStyles;
    }

    private void updateRowFill(Paint fill) {
        row.applyBackground(fill);
    }
}