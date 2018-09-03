package webfx.framework.services.authz.mixin;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import webfx.fxkits.core.spi.FxKit;
import webfx.platforms.core.util.async.AsyncFunction;
import webfx.platforms.core.util.function.Function;

/**
 * @author Bruno Salmon
 */
public class AuthorizationUtil {

    public static <C, Rq> ObservableBooleanValue authorizedOperationProperty(Function<C, Rq> operationRequestFactory, AsyncFunction<Rq, Boolean> authorizationFunction, ObservableValue<C> contextProperty, ObservableValue userPrincipalProperty) {
        return new BooleanBinding() {
            C context;
            Object userPrincipal;
            Boolean value;
            { bind(contextProperty, userPrincipalProperty); }

            @Override
            protected void onInvalidating() {
                C context = contextProperty.getValue();
                Object userPrincipal = userPrincipalProperty == null ? null : userPrincipalProperty.getValue();
                if (this.context != context || this.userPrincipal != userPrincipal || value == null) {
                    value = false;
                    authorizationFunction.apply(operationRequestFactory.apply(context)).setHandler(ar -> {
                        this.context = context;
                        this.userPrincipal = userPrincipal;
                        if (ar.succeeded())
                            FxKit.get().scheduler().runInUiThread(() -> {
                                value = ar.result();
                                invalidate();
                            });
                    });
                }
            }

            @Override
            protected boolean computeValue() {
                if (value == null)
                    onInvalidating();
                return value;
            }
        };
    }

}