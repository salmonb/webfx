package naga.providers.toolkit.swing.drawing.view;

import naga.providers.toolkit.swing.util.SwingTransforms;
import naga.toolkit.drawing.shapes.Drawable;
import naga.toolkit.drawing.shapes.Point2D;
import naga.toolkit.drawing.spi.impl.canvas.CanvasDrawableView;
import naga.toolkit.drawing.spi.view.base.DrawableViewBase;
import naga.toolkit.drawing.spi.view.base.DrawableViewImpl;
import naga.toolkit.drawing.spi.view.base.DrawableViewMixin;
import naga.toolkit.spi.events.MouseEvent;
import naga.toolkit.spi.events.UiEventHandler;
import naga.toolkit.transform.Transform;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collection;


/**
 * @author Bruno Salmon
 */
public abstract class SwingDrawableView
        <D extends Drawable, DV extends DrawableViewBase<D, DV, DM>, DM extends DrawableViewMixin<D, DV, DM>>

        extends DrawableViewImpl<D, DV, DM>
        implements CanvasDrawableView<D, Graphics2D> {

    private AffineTransform swingTransform;

    SwingDrawableView(DV base) {
        super(base);
    }

    @Override
    public void prepareCanvasContext(Graphics2D g) {
        if (swingTransform != null) {
            AffineTransform tx = new AffineTransform(g.getTransform());
            tx.concatenate(swingTransform);
            g.setTransform(tx);
        }
    }

    @Override
    public void updateLocalToParentTransforms(Collection<Transform> localToParentTransforms) {
        swingTransform = SwingTransforms.toSwingTransform(localToParentTransforms);
    }

    @Override
    public void updateOnMouseClicked(UiEventHandler<? super MouseEvent> onMouseClicked) {
    }

    @Override
    public void paint(Graphics2D g) {
    }

    @Override
    public boolean containsPoint(Point2D point) {
        return false;
    }
}
