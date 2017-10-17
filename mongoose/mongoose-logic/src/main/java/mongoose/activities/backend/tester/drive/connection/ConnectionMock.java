package mongoose.activities.backend.tester.drive.connection;


import mongoose.activities.backend.tester.drive.command.Command;
import mongoose.activities.backend.monitor.listener.ConnectionEvent;
import mongoose.activities.backend.monitor.listener.EventType;
import naga.platform.spi.Platform;
import naga.scheduler.Scheduler;

/**
 * @author Jean-Pierre Alonso.
 */
public class ConnectionMock extends ConnectionBase {

    @Override
    public ConnectionEvent executeCommand(Command t) {
        Scheduler scheduler = Platform.get().scheduler();
        ConnectionEvent event;
        switch (t) {
            case OPEN:
                event = new ConnectionEvent(EventType.CONNECTING);
                scheduler.scheduleDelay(1000, SimConnection);
                break;
            case CLOSE:
                event = new ConnectionEvent(EventType.DISCONNECTING);
                scheduler.scheduleDelay(500, SimClose);
                break;
            default:
                event = null;
        }
        applyEvent(event);
        recordEvent(event);
        return event;
    }

    private Runnable SimConnection = () -> {
        // Synchronous ?
        ConnectionEvent event = new ConnectionEvent(EventType.CONNECTED);
        applyEvent(event);
        recordEvent(event);
        Platform.log("CnxMock-CONNECTED");
    };

    private Runnable SimClose = () -> {
        // Synchronous ?
        ConnectionEvent event = new ConnectionEvent(EventType.NOT_CONNECTED);
        applyEvent(event);
        recordEvent(event);
        Platform.log("CnxMock-CLOSED");
    };
}
