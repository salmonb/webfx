package naga.providers.toolkit.html.drawing.svg.view;

import naga.providers.toolkit.html.util.SvgUtil;
import naga.toolkit.drawing.layout.Region;
import naga.toolkit.drawing.spi.view.base.RegionViewBase;
import naga.toolkit.drawing.spi.view.base.RegionViewMixin;

/**
 * @author Bruno Salmon
 */
public class SvgRegionView
        extends SvgNodeView<Region, RegionViewBase, RegionViewMixin>
        implements RegionViewMixin {

    public SvgRegionView() {
        super(new RegionViewBase(), SvgUtil.createSvgGroup());
    }
}