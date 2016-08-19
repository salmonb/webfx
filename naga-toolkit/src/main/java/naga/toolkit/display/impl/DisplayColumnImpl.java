package naga.toolkit.display.impl;

import naga.commons.type.Type;
import naga.toolkit.cell.renderers.ValueRenderer;
import naga.toolkit.cell.renderers.ValueRendererFactory;
import naga.toolkit.display.DisplayColumn;
import naga.toolkit.display.DisplayStyle;
import naga.toolkit.display.Label;

/**
 * @author Bruno Salmon
 */
public class DisplayColumnImpl implements DisplayColumn {

    private final Object headerValue;
    private final Label label;
    private final Type type;
    private final String role;
    private final DisplayStyle style;
    private ValueRenderer valueRenderer;

    public DisplayColumnImpl(Object label, Type type) {
        this(label, label, type, null, null, null);
    }

    public DisplayColumnImpl(Object headerValue, Object label, Type type, String role, DisplayStyle style, ValueRenderer valueRenderer) {
        this.headerValue = headerValue;
        this.label = Label.from(label);
        this.type = type;
        this.role = role;
        this.style = style != null ? style : DisplayStyle.NO_STYLE;
        this.valueRenderer = valueRenderer;
    }

    @Override
    public Object getHeaderValue() {
        return headerValue;
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public String getName() {
        return label.getText();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public DisplayStyle getStyle() {
        return style;
    }

    public ValueRenderer getValueRenderer() {
        if (valueRenderer == null)
            valueRenderer = ValueRendererFactory.getDefault().createCellRenderer(getType());
        return valueRenderer;
    }
}
