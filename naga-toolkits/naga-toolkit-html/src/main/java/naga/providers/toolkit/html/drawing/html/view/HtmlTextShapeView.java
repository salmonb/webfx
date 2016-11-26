package naga.providers.toolkit.html.drawing.html.view;

import naga.providers.toolkit.html.util.HtmlPaints;
import naga.providers.toolkit.html.util.HtmlUtil;
import naga.toolkit.drawing.geometry.VPos;
import naga.toolkit.drawing.paint.Paint;
import naga.toolkit.drawing.spi.view.base.TextShapeViewBase;
import naga.toolkit.drawing.spi.view.base.TextShapeViewMixin;
import naga.toolkit.drawing.text.Font;
import naga.toolkit.drawing.text.TextAlignment;
import naga.toolkit.drawing.text.TextShape;

/**
 * @author Bruno Salmon
 */
public class HtmlTextShapeView
        extends HtmlShapeView<TextShape, TextShapeViewBase, TextShapeViewMixin>
        implements TextShapeViewMixin {

    public HtmlTextShapeView() {
        super(new TextShapeViewBase(), HtmlUtil.createAbsolutePositionSpan());
        setElementStyleAttribute("pointer-events", "none"); // To make it mouse transparent
        setElementStyleAttribute("line-height", "100%");
    }

    @Override
    public void updateText(String text) {
        setElementTextContent(text);
        updateY();
    }

    @Override
    public void updateFill(Paint fill) {
        getElement().style.color = HtmlPaints.toHtmlCssPaint(fill);
    }

    @Override
    public void updateTextOrigin(VPos textOrigin) {
        updateY();
    }

    @Override
    public void updateX(Double x) {
        setElementStyleAttribute("left", toPx(x));
    }

    @Override
    public void updateY(Double y) {
        VPos textOrigin = getNode().getTextOrigin();
        if (textOrigin != null && textOrigin != VPos.TOP) {
            double clientHeight = getElement().clientHeight;
            if (textOrigin == VPos.CENTER)
                y = y - clientHeight / 2;
            else if (textOrigin == VPos.BOTTOM)
                y = y - clientHeight;
        }
        setElementStyleAttribute("top", toPx(y));
    }

    private void updateY() {
        updateY(getNode().getY());
    }

    @Override
    public void updateWrappingWidth(Double wrappingWidth) {
        if (wrappingWidth != null)
            setElementStyleAttribute("width", toPx(wrappingWidth));
        updateY();
    }

    @Override
    public void updateTextAlignment(TextAlignment textAlignment) {
        setElementStyleAttribute("text-align", toCssTextAlignment(textAlignment));
    }

    @Override
    public void updateFont(Font font) {
        setHtmlFontAttributes(font);
        updateY();
    }
}