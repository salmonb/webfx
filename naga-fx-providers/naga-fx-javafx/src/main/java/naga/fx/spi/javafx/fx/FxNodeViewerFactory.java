package naga.fx.spi.javafx.fx;

import naga.fx.spi.javafx.fx.viewer.*;
import naga.fx.scene.Group;
import naga.fx.scene.control.*;
import naga.fx.scene.image.ImageView;
import naga.fx.scene.layout.*;
import naga.fx.scene.shape.Circle;
import naga.fx.scene.shape.Line;
import naga.fx.scene.shape.Rectangle;
import naga.fx.scene.text.Text;
import naga.fx.spi.viewer.base.NodeViewerFactoryImpl;
import naga.fxdata.chart.*;
import naga.fxdata.control.DataGrid;
import naga.fxdata.control.HtmlText;

/**
 * @author Bruno Salmon
 */
public class FxNodeViewerFactory extends NodeViewerFactoryImpl {

    public final static FxNodeViewerFactory SINGLETON = new FxNodeViewerFactory();

    protected FxNodeViewerFactory() {
        registerNodeViewerFactory(Rectangle.class, FxRectangleViewer::new);
        registerNodeViewerFactory(Circle.class, FxCircleViewer::new);
        registerNodeViewerFactory(Line.class, FxLineViewer::new);
        registerNodeViewerFactory(Text.class, FxTextViewer::new);
        registerNodeViewerFactory(Label.class, FxLabelViewer::new);
        registerNodeViewerFactory(Group.class, FxGroupViewer::new);
        registerNodeViewerFactory(Region.class, FxLayoutViewer::new);
        registerNodeViewerFactory(VBox.class, FxLayoutViewer::new);
        registerNodeViewerFactory(HBox.class, FxLayoutViewer::new);
        registerNodeViewerFactory(BorderPane.class, FxLayoutViewer::new);
        registerNodeViewerFactory(FlowPane.class, FxLayoutViewer::new);
        registerNodeViewerFactory(GridPane.class, FxLayoutViewer::new);
        registerNodeViewerFactory(Button.class, FxButtonViewer::new);
        registerNodeViewerFactory(CheckBox.class, FxCheckBoxViewer::new);
        registerNodeViewerFactory(RadioButton.class, FxRadioButtonViewer::new);
        registerNodeViewerFactory(TextField.class, FxTextFieldViewer::new);
        registerNodeViewerFactory(ImageView.class, FxImageViewViewer::new);
        registerNodeViewerFactory(HtmlText.class, FxHtmlTextViewer::new);
        registerNodeViewerFactory(Slider.class, FxSliderViewer::new);
        registerNodeViewerFactory(DataGrid.class, FxDataGridViewer::new);
        registerNodeViewerFactory(AreaChart.class, FxAreaChartViewer::new);
        registerNodeViewerFactory(BarChart.class, FxBarChartViewer::new);
        registerNodeViewerFactory(LineChart.class, FxLineChartViewer::new);
        registerNodeViewerFactory(PieChart.class, FxPieChartViewer::new);
        registerNodeViewerFactory(ScatterChart.class, FxScatterChartViewer::new);
    }
}