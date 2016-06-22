package mongoose.activities.organizations;

import naga.core.spi.platform.Platform;
import naga.core.spi.toolkit.Toolkit;
import naga.core.spi.toolkit.nodes.BorderPane;
import naga.core.spi.toolkit.nodes.CheckBox;
import naga.core.spi.toolkit.nodes.SearchBox;
import naga.core.spi.toolkit.nodes.Table;
import naga.core.ui.presentation.PresentationActivity;
import naga.core.ui.rx.RxFilter;

/**
 * @author Bruno Salmon
 */
public class OrganizationsActivity extends PresentationActivity<OrganizationsViewModel, OrganizationsPresentationModel> {

    public OrganizationsActivity() {
        super(OrganizationsPresentationModel::new);
    }

    protected OrganizationsViewModel buildView(Toolkit toolkit) {
        // Building the UI components
        SearchBox searchBox = toolkit.createNode(SearchBox.class);
        Table table = toolkit.createNode(Table.class);
        CheckBox limitCheckBox = toolkit.createNode(CheckBox.class);

        return new OrganizationsViewModel(toolkit.createNode(BorderPane.class)
                .setTop(searchBox)
                .setCenter(table)
                .setBottom(limitCheckBox)
                , searchBox, table, limitCheckBox);
    }

    protected void bindViewModelWithPresentationModel(OrganizationsViewModel vm, OrganizationsPresentationModel pm) {
        // Hard coded initialization
        SearchBox searchBox = vm.getSearchBox();
        CheckBox limitCheckBox = vm.getLimitCheckBox();
        searchBox.setPlaceholder("Enter your centre name to narrow the list");
        searchBox.requestFocus();
        limitCheckBox.setText("Limit to 100");

        // Initialization from the presentation model current state
        searchBox.setText(pm.searchTextProperty().getValue());
        limitCheckBox.setSelected(pm.limitProperty().getValue());

        // Binding the UI with the presentation model for further state changes
        // User inputs: the UI state changes are transferred in the presentation model
        pm.searchTextProperty().bind(searchBox.textProperty());
        pm.limitProperty().bind(limitCheckBox.selectedProperty());
        pm.organizationsDisplaySelectionProperty().bind(vm.getTable().displaySelectionProperty());
        // User outputs: the presentation model changes are transferred in the UI
        vm.getTable().displayResultSetProperty().bind(pm.organizationsDisplayResultSetProperty());
    }

    protected void bindPresentationModelWithLogic(OrganizationsPresentationModel pm) {
        // Loading the domain model and setting up the reactive filter
        RxFilter rxFilter = createRxFilter("{class: 'Organization', where: '!closed', orderBy: 'name'}")
                // Search box condition
                .combine(pm.searchTextProperty(), s -> s == null ? null : "{where: 'lower(name) like `%" + s.toLowerCase() + "%`'}")
                // Limit condition
                .combine(pm.limitProperty(), "{limit: '100'}")
                .setExpressionColumns("[" +
                        "{label: 'Name', expression: 'name + ` (` + type.code + `)`'}," +
                        "{label: 'Country', expression: 'country.(name + ` (` + continent.name + `)`)'}" +
                        "]")
                .displayResultSetInto(pm.organizationsDisplayResultSetProperty());

        pm.organizationsDisplaySelectionProperty().addListener((observable, oldValue, newValue) -> {
            int selectedRow = newValue.getSelectedRow();
            Platform.log("Selected row: " + selectedRow);
            if (selectedRow >= 0)
                Platform.log("Selected entity: " + rxFilter.getCurrentEntityList().get(selectedRow));
        });
    }
}
