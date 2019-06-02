package webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.svg;

import com.google.gwt.core.client.JavaScriptObject;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import webfx.fxkit.javafxgraphics.mapper.highcoupling.spi.impl.ScenePeerBase;
import webfx.fxkit.javafxgraphics.mapper.spi.NodePeer;
import webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.html.HtmlNodePeer;
import webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.shared.HtmlSvgNodePeer;
import webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.shared.SvgRoot;
import webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.shared.SvgRootBase;
import webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.util.HtmlPaints;
import webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.util.HtmlUtil;
import webfx.fxkit.javafxgraphics.mapper.spi.impl.peer.gwt.util.SvgUtil;
import webfx.fxkit.util.properties.Properties;
import webfx.platform.shared.util.Numbers;
import webfx.platform.shared.util.collection.Collections;

/**
 * @author Bruno Salmon
 */
public final class SvgScenePeer extends ScenePeerBase implements SvgRoot {

    private final Element container = SvgUtil.createSvgElement();
    private final SvgRoot svgRootDelegate = new SvgRootBase();

    public SvgScenePeer(Scene scene) {
        super(scene);
        HtmlUtil.setAttribute(container, "width", "100%");
        Properties.runNowAndOnPropertiesChange(property -> updateContainerWidth(), scene.widthProperty());
        Properties.runNowAndOnPropertiesChange(property -> updateContainerHeight(), scene.heightProperty());
        Properties.runNowAndOnPropertiesChange(property -> updateContainerFill(), scene.fillProperty());
    }

    private void updateContainerWidth() {
        double width = Numbers.doubleValue(scene.getWidth());
        HtmlUtil.setAttribute(container, "width",
                (width > 0 ?
                        width :
                        scene.getRoot() != null ?
                                scene.getRoot().prefWidth(-1) :
                                0)
                        + "px");
    }

    private void updateContainerHeight() {
        double height = Numbers.doubleValue(scene.getHeight());
        HtmlUtil.setAttribute(container, "height",
                (height > 0 ?
                        height :
                        scene.getRoot() != null ?
                                scene.getRoot().prefHeight(-1) :
                                0)
                        + "px");
    }

    private void updateContainerFill() {
        HtmlUtil.setAttribute(container, "fill", toPaintAttribute(scene.getFill()));
    }

    private String toPaintAttribute(Paint paint) {
        String value = null;
        if (paint instanceof Color)
            value = HtmlPaints.toSvgCssPaint(paint);
        else if (paint instanceof LinearGradient) {
            Element svgLinearGradient = addDef(SvgUtil.createLinearGradient());
            SvgUtil.updateLinearGradient((LinearGradient) paint, svgLinearGradient);
            value = SvgUtil.getDefUrl(svgLinearGradient);
        }
        return value;
    }

    @Override
    public Element getDefsElement() {
        return svgRootDelegate.getDefsElement();
    }

    @Override
    public Element addDef(Element def) {
        return svgRootDelegate.addDef(def);
    }

    @Override
    public NodePeer pickPeer(double sceneX, double sceneY) {
        Element element = DomGlobal.document.elementFromPoint(sceneX, sceneY);
        return (NodePeer) HtmlUtil.getJsJavaObjectAttribute((JavaScriptObject) (Object) element, "peer");
    }

    @Override
    public void onRootBound() {
        HtmlUtil.setChildren(container, getDefsElement(), HtmlSvgNodePeer.toContainerElement(scene.getRoot(), scene));
    }

    @Override
    public void updateParentAndChildrenPeers(Parent parent, ListChangeListener.Change<Node> childrenChange) {
        HtmlSvgNodePeer parentPeer = HtmlSvgNodePeer.toNodePeer(parent, scene);
        HtmlUtil.setChildren(parentPeer.getChildrenContainer(), Collections.map(parent.getChildren(), node -> HtmlSvgNodePeer.toContainerElement(node, scene)));
    }

    @Override
    public void onNodePeerCreated(NodePeer<Node> nodePeer) {
        if (nodePeer instanceof HtmlNodePeer) {
            HtmlNodePeer htmlNodeView = (HtmlNodePeer) nodePeer;
            HTMLElement htmlElement = (HTMLElement) htmlNodeView.getElement();
            Element foreignObject = SvgUtil.createSvgElement("foreignObject");
            foreignObject.setAttribute("width", "100%");
            foreignObject.setAttribute("height", "100%");
            HtmlUtil.setChild(foreignObject, htmlElement);
            htmlNodeView.setContainer(foreignObject);
        }
    }
}