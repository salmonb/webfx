package mongooses.loadtester.operations.backend.route;

import mongooses.loadtester.activities.loadtester.LoadTesterRouting;
import webfx.framework.operation.HasOperationCode;
import webfx.framework.operations.route.RoutePushRequest;
import webfx.platforms.core.services.browsinghistory.spi.BrowsingHistory;

/**
 * @author Bruno Salmon
 */
public final class RouteToTesterRequest extends RoutePushRequest implements HasOperationCode {

    private final static String OPERATION_CODE = "RouteToTester";

    public RouteToTesterRequest(BrowsingHistory history) {
        super(LoadTesterRouting.getPath(), history);
    }

    @Override
    public Object getOperationCode() {
        return OPERATION_CODE;
    }
}