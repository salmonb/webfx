package mongoose.activities.backend.tester;

import naga.framework.activity.combinations.domainpresentation.impl.DomainPresentationActivityImpl;

/**
 * @author Bruno Salmon
 */
public class TesterPresentationActivity extends DomainPresentationActivityImpl<TesterPresentationModel> {

    public TesterPresentationActivity() {
        super(TesterPresentationViewActivity::new, TesterPresentationLogicActivity::new);
    }
}