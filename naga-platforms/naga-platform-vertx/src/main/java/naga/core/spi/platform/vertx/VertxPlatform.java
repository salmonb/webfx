package naga.core.spi.platform.vertx;

import io.vertx.core.Vertx;
import naga.core.json.JsonFactory;
import naga.core.spi.bus.BusFactory;
import naga.core.spi.bus.vertx.VertxBusFactory;
import naga.core.spi.platform.Platform;
import naga.core.spi.platform.java.JavaPlatform;
import naga.core.spi.sql.SqlService;
import naga.core.spi.sql.vertx.VertxSqlService;

/**
 * @author Bruno Salmon
 */
public final class VertxPlatform extends JavaPlatform {

    public static void register(Vertx vertx) {
        Platform.register(new VertxPlatform(vertx));
    }

    private final JsonFactory jsonFactory = new VertxJsonObject();
    private final BusFactory busFactory;
    private final SqlService sqlService;

    public VertxPlatform(Vertx vertx) {
        super(new VertxScheduler(vertx));
        busFactory = new VertxBusFactory(vertx.eventBus());
        sqlService = new VertxSqlService(vertx);
    }

    @Override
    public JsonFactory jsonFactory() {
        return jsonFactory;
    }

    @Override
    public BusFactory busFactory() {
        return busFactory;
    }

    @Override
    public SqlService sqlService() {
        return sqlService;
    }
}
