package naga.toolkit.fx.spi.view.base;

import naga.commons.type.PrimType;
import naga.commons.type.Type;
import naga.commons.util.Strings;
import naga.toolkit.display.DisplayColumn;
import naga.toolkit.display.DisplayResultSet;
import naga.toolkit.fx.ext.chart.Chart;
import naga.toolkit.fx.spi.view.ChartView;

/**
 * @author Bruno Salmon
 */
public abstract class ChartViewBase
        <C, N extends Chart, NV extends ChartViewBase<C, N, NV, NM>, NM extends ChartViewMixin<C, N, NV, NM>>

        extends SelectableDisplayResultSetControlViewBase<C, N, NV, NM>
        implements ChartView<N> {

    private final boolean isPieChart = this instanceof PieChartViewBase;

    void updateResultSet(DisplayResultSet rs) {
        if (rs == null)
            return;
        int rowCount = rs.getRowCount();
        int columnCount = rs.getColumnCount();
        DisplayColumn[] columns = rs.getColumns();
        boolean rowFormat = "series".equals(columns[0].getRole());
        boolean hasXAxis = !isPieChart;
        if (!rowFormat) { /***** Column format - see {@link Chart} for format description *****/
            if (isPieChart && rowCount > 1) // ignoring extra rows for pie chart
                rowCount = 1;
            int firstSeriesColumnIndex = hasXAxis ? 1 : 0;
            int seriesCount = columnCount - firstSeriesColumnIndex;
            int pointPerSeriesCount = rowCount;
            Type xType = hasXAxis ? columns[0].getType() : null;
            Type yType = columns[firstSeriesColumnIndex].getType();
            mixin.createChartData(xType, yType, pointPerSeriesCount, seriesCount, seriesIndex -> columns[firstSeriesColumnIndex + seriesIndex].getName());
            for (int pointIndex = 0; pointIndex < pointPerSeriesCount; pointIndex++) {
                if (hasXAxis)
                    mixin.setChartDataX(rs.getValue(pointIndex, 0), pointIndex);
                for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                    Object yValue = rs.getValue(pointIndex, firstSeriesColumnIndex + seriesIndex);
                    mixin.setChartDataY(yValue, pointIndex, seriesIndex);
                }
            }
        } else {          /***** Row format - see {@link Chart} for format description *****/
            if (isPieChart && columnCount > 2) // ignoring extra columns for pie charts
                columnCount = 2;
            int seriesCount = rowCount;
            int pointPerSeriesCount = columnCount - 1;
            Type xType = hasXAxis ? PrimType.fromObject(columns[1].getName()) : null;
            Type yType = columns[pointPerSeriesCount].getType();
            mixin.createChartData(xType, yType, pointPerSeriesCount, seriesCount, seriesIndex -> Strings.toString(rs.getValue(seriesIndex, 0)));
            for (int pointIndex = 0; pointIndex < pointPerSeriesCount; pointIndex++) {
                if (hasXAxis)
                    mixin.setChartDataX(columns[pointIndex + 1].getName(), pointIndex);
                for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                    Object yValue = rs.getValue(seriesIndex, pointIndex + 1);
                    mixin.setChartDataY(yValue, pointIndex, seriesIndex);
                }
            }
        }
        mixin.applyChartData();
    }

}