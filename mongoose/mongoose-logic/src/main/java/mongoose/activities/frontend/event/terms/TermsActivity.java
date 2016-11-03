package mongoose.activities.frontend.event.terms;

import mongoose.activities.frontend.event.shared.BookingProcessActivity;

/**
 * @author Bruno Salmon
 */
public class TermsActivity extends BookingProcessActivity<TermsViewModel, TermsPresentationModel> {

    public TermsActivity() {
        super(TermsPresentationModel::new, null);
        registerViewBuilder(getClass(), new TermsViewModelBuilder());
    }

    @Override
    protected void bindViewModelWithPresentationModel(TermsViewModel vm, TermsPresentationModel pm) {
        super.bindViewModelWithPresentationModel(vm, pm);
        vm.getTermsLetterDisplayResultSetNode().displayResultSetProperty().bind(pm.termsLetterDisplayResultSetProperty());
    }

    @Override
    protected void bindPresentationModelWithLogic(TermsPresentationModel pm) {
        // Loading the domain model and setting up the reactive filter
        createReactiveExpressionFilter("{class: 'Letter', where: 'type.terms', limit: '1'}")
                .combine(pm.eventIdProperty(), e -> "{where: 'event=" + e + "'}")
                .combine(getI18n().languageProperty(), lang -> "{columns: '[`html(" + lang + ")`]'}")
                .displayResultSetInto(pm.termsLetterDisplayResultSetProperty())
                .start();
    }
}
