package mongoose.activities.backend.bookings;

import naga.framework.operation.HasOperationCode;
import naga.framework.ui.router.PushRoutingRequest;
import naga.platform.client.url.history.History;

/**
 * @author Bruno Salmon
 */
public class BookingsRoutingRequest extends PushRoutingRequest implements HasOperationCode {

    private final static String OPERATION_CODE = "BOOKINGS_ROUTING";

    public BookingsRoutingRequest(Object eventId, History history) {
        super(BookingsRouting.getEventBookingsPath(eventId), history);
    }

    @Override
    public Object getOperationCode() {
        return OPERATION_CODE;
    }
}