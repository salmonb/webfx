package mongooses.core.sharedends.services.authz;

import mongooses.core.shared.domainmodel.loader.DomainModelSnapshotLoader;
import webfx.framework.orm.domainmodel.DataSourceModel;
import webfx.framework.services.authz.spi.impl.AuthorizationServiceProviderBase;
import webfx.framework.services.authz.spi.impl.UserPrincipalAuthorizationChecker;

/**
 * @author Bruno Salmon
 */
public final class MongooseAuthorizationServiceProvider extends AuthorizationServiceProviderBase {

    private final DataSourceModel dataSourceModel;

    public MongooseAuthorizationServiceProvider() {
        this(DomainModelSnapshotLoader.getDataSourceModel());
    }

    public MongooseAuthorizationServiceProvider(DataSourceModel dataSourceModel) {
        this.dataSourceModel = dataSourceModel;
    }

    @Override
    protected UserPrincipalAuthorizationChecker createUserPrincipalAuthorizationChecker(Object userPrincipal) {
        return new MongooseInMemoryUserPrincipalAuthorizationChecker(userPrincipal, dataSourceModel);
    }
}