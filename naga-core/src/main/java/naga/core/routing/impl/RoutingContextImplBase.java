package naga.core.routing.impl;

import naga.core.routing.Route;
import naga.core.routing.RoutingContext;
import naga.core.spi.platform.Platform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Bruno Salmon
 */
abstract class RoutingContextImplBase implements RoutingContext {

    protected final String mountPoint;
    protected final String path;
    protected final Collection<RouteImpl> routes;
    protected Iterator<RouteImpl> iter;
    protected Route currentRoute;
    private Map<String, String> params;

    RoutingContextImplBase(String mountPoint, String path, Collection<RouteImpl> routes) {
        this.mountPoint = mountPoint;
        this.path = path;
        this.routes = routes;
        iter = routes.iterator();
    }

    @Override
    public String mountPoint() {
        return mountPoint;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public Route currentRoute() {
        return currentRoute;
    }

    @Override
    public void next() {
        iterateNext();
    }

    boolean iterateNext() {
        boolean failed = failed();
        while (iter.hasNext()) {
            RouteImpl route = iter.next();
            if (route.matches(this, mountPoint(), failed)) {
                //Platform.log("Route matches: " + route);
                try {
                    currentRoute = route;
                    //Platform.log("Calling the " + (failed ? "failure" : "") + " handler");
                    if (failed)
                        route.handleFailure(this);
                    else
                        route.handleContext(this);
                } catch (Throwable t) {
                    Platform.log("Throwable thrown from handler", t);
                    if (!failed) {
                        Platform.log("Failing the routing");
                        fail(t);
                    } else {
                        // Failure in handling failure!
                        Platform.log("Failure in handling failure");
                        unhandledFailure(-1, t, route.router());
                    }
                } finally {
                    currentRoute = null;
                }
                return true;
            }
        }
        return false;
    }

    void unhandledFailure(int statusCode, Throwable failure, RouterImpl router) {
        //int code = statusCode != -1 ? statusCode : 500;
        if (failure != null) {
            if (router.exceptionHandler() != null)
                router.exceptionHandler().handle(failure);
            else
                Platform.log("Unexpected exception in route", failure);
        }
        /*if (!response().ended()) {
            try {
                response().setStatusCode(code);
            } catch (IllegalArgumentException e) {
                // means that there are invalid chars in the status message
                response()
                        .setStatusMessage(HttpResponseStatus.valueOf(code).reasonPhrase())
                        .setStatusCode(code);
            }
            response().end(response().getStatusMessage());
        }*/
    }

    @Override
    public Map<String, String> getParams() {
        if (params == null)
            params = new HashMap<>();
        return params;
    }
}
