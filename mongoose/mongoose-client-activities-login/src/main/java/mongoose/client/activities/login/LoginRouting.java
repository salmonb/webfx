package mongoose.client.activities.login;

import webfx.framework.client.activity.impl.combinations.viewdomain.impl.ViewDomainActivityContextFinal;
import webfx.framework.client.ui.uirouter.ProvidedLoginUiRoute;
import webfx.framework.client.ui.uirouter.UiRoute;
import webfx.framework.client.ui.uirouter.impl.UiRouteImpl;

/**
 * @author Bruno Salmon
 */
public final class LoginRouting {

    private static final String PATH = "/login";

    public static String getPath() {
        return PATH;
    }

}