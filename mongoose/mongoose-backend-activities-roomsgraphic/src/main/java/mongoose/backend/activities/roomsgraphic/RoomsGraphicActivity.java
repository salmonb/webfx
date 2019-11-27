package mongoose.backend.activities.roomsgraphic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import mongoose.backend.controls.bookingdetailspanel.BookingDetailsPanel;
import mongoose.backend.controls.masterslave.MasterSlaveView;
import mongoose.backend.operations.entities.resourceconfiguration.*;
import mongoose.client.activity.eventdependent.EventDependentViewDomainActivity;
import mongoose.client.presentationmodel.HasSelectedDocumentProperty;
import mongoose.shared.entities.Document;
import mongoose.shared.entities.DocumentLine;
import mongoose.shared.entities.Site;
import webfx.extras.imagestore.ImageStore;
import webfx.extras.visual.controls.grid.SkinnedVisualGrid;
import webfx.extras.visual.controls.grid.VisualGrid;
import webfx.framework.client.operation.action.OperationActionFactoryMixin;
import webfx.framework.client.orm.entity.filter.ReactiveEntityFilter;
import webfx.framework.client.orm.entity.filter.visual.ReactiveVisualFilter;
import webfx.framework.client.orm.entity.filter.visual.ReactiveVisualFilterFactoryMixin;
import webfx.framework.client.orm.mapping.entity_to_object.ObservableEntitiesToObjectsMapper;
import webfx.framework.client.ui.action.ActionGroup;
import webfx.framework.client.ui.layouts.FlexBox;
import webfx.framework.client.ui.layouts.LayoutUtil;
import webfx.framework.shared.orm.entity.Entity;
import webfx.framework.shared.orm.entity.EntityId;
import webfx.kit.util.properties.ObservableLists;
import webfx.kit.util.properties.Properties;
import webfx.platform.shared.services.json.Json;
import webfx.platform.shared.services.serial.SerialCodecManager;

import java.util.List;
import java.util.Objects;

import static webfx.framework.client.orm.dql.DqlStatement.where;

final class RoomsGraphicActivity extends EventDependentViewDomainActivity implements
        ReactiveVisualFilterFactoryMixin,
        HasSelectedDocumentProperty,
        OperationActionFactoryMixin {

    private final ObjectProperty<Document> selectedDocumentProperty = new SimpleObjectProperty<>();
    @Override public ObjectProperty<Document> selectedDocumentProperty() { return selectedDocumentProperty; }

    private Pane container; // Keeping a reference of the container to act as the parent for dialog windows
    private ReactiveEntityFilter<Site> sitesFilter;

    @Override
    public Node buildUi() {
        TabPane sitesTabPane = new TabPane();
        ObservableEntitiesToObjectsMapper<Site, SiteTabController> sitesToTabControllersMapper = new ObservableEntitiesToObjectsMapper<>(SiteTabController::new, (site, controller) -> controller.update(site), (site, controller) -> controller.delete());
        ObservableLists.bindConverted(sitesTabPane.getTabs(), sitesToTabControllersMapper.getObservableObjects(), SiteTabController::getTab);
        MasterSlaveView masterSlaveView = new MasterSlaveView(sitesTabPane, MasterSlaveView.createAndBindSlaveViewIfApplicable(this, this, () -> container).buildUi());
        masterSlaveView.slaveVisibleProperty().bind(Properties.compute(selectedDocumentProperty(), Objects::nonNull));
        // Setting up the master filter that controls the content displayed in the master view
        sitesFilter = this.<Site>createReactiveEntityFilter("{class: 'Site', alias: 's', fields: 'icon,name', where: `exists(select ResourceConfiguration where resource.site=s and item.family.code='acco')`, orderBy: 'ord,id'}")
                // Applying the event condition
                .combineIfNotNullOtherwiseForceEmptyResult(getPresentationModel().eventIdProperty(), eventId -> where("event=?", eventId))
                .setEntitiesHandler(sitesToTabControllersMapper::updateFromEntities)
                // Activating server push notification
                .setPush(true)
                // Everything set up, let's start now!
                .start();
        return container = masterSlaveView.buildUi();
    }

    class SiteTabController {

        final TabPane itemTabPane = new TabPane();
        final Tab siteTab = new Tab(null, itemTabPane);
        final ReactiveEntityFilter<Entity> itemsFilter;

        SiteTabController(Site site) {
            siteTab.setClosable(false);
            Object sitePk = site.getPrimaryKey();
            itemsFilter = createReactiveEntityFilter(sitesFilter,"{class: 'ResourceConfiguration', fields: 'item.icon,item.name,resource.site', where: `resource.site=" + sitePk + " and item.family.code='acco'`, groupBy: 'item', orderBy: 'item.ord,item.id'}");
            ObservableEntitiesToObjectsMapper<Entity, ItemTabController> resourcesToItemTabControllersMapper = new ObservableEntitiesToObjectsMapper<>(rc -> new ItemTabController(rc, site, itemsFilter), (rc, controller) -> controller.update(rc), (rc, controller) -> controller.delete());
            ObservableLists.bindConverted(itemTabPane.getTabs(), resourcesToItemTabControllersMapper.getObservableObjects(), ItemTabController::getTab);
            itemsFilter.setEntitiesHandler(resourcesToItemTabControllersMapper::updateFromEntities)
                    .bindActivePropertyTo(siteTab.selectedProperty())
                    .setPush(true)
                    .start();
            update(site);
        }

        void update(Site site) {
            siteTab.setText(site.getName());
            siteTab.setGraphic(ImageStore.createImageView(site.getIcon()));
        }

        void delete() {
            itemsFilter.stop();
        }

        Tab getTab() {
            return siteTab;
        }
    }

    class ItemTabController {

        final Pane resourcesBoxContainer = new FlexBox(10, 10);
        final Tab itemTab = new Tab(null, LayoutUtil.createVerticalScrollPane(resourcesBoxContainer));
        final ReactiveEntityFilter<Entity> boxesFilter;

        ItemTabController(Entity resourceConfiguration, Site site, ReactiveEntityFilter<?> parentFilter) {
            resourcesBoxContainer.setPadding(new Insets(10));
            itemTab.setClosable(false);
            boxesFilter = createReactiveEntityFilter(parentFilter,"{class: 'ResourceConfiguration', fields: 'name,online,max,comment', where: `resource.site=" + ((EntityId) resourceConfiguration.evaluate("resource.site")).getPrimaryKey() + " and item=" + ((EntityId) resourceConfiguration.evaluate("item")).getPrimaryKey() + "`, orderBy: 'name'}");
            ObservableEntitiesToObjectsMapper<Entity, ResourceBoxController> resourcesToBoxControllersMapper = new ObservableEntitiesToObjectsMapper<>(rc -> new ResourceBoxController(rc, site, resourcesBoxContainer, boxesFilter), (rc, box) -> box.update(rc), (rc, box) -> box.delete());
            ObservableLists.bindConverted(resourcesBoxContainer.getChildren(), resourcesToBoxControllersMapper.getObservableObjects(), ResourceBoxController::getNode);
            boxesFilter.setEntitiesHandler(resourcesToBoxControllersMapper::updateFromEntities)
                    .bindActivePropertyTo(itemTab.selectedProperty())
                    .setPush(true)
                    .start();
            update(resourceConfiguration);
        }

        void update(Entity resourceConfiguration) {
            itemTab.setText((String) resourceConfiguration.evaluate("item.name"));
            itemTab.setGraphic(ImageStore.createImageView((String) resourceConfiguration.evaluate("item.icon")));
        }

        void delete() {
            boxesFilter.stop();
        }

        Tab getTab() {
            return itemTab;
        }
    }

    private final static CornerRadii BOX_RADII = new CornerRadii(8);
    private final static Color ONLINE_BOX_COLOR = Color.color(0, 0.8, 0);
    private final static Color OFFLINE_BOX_COLOR = Color.ORANGE;
    private final static Color DRAG_BOX_COLOR = Color.BLUEVIOLET;
    private final static Border BOX_BORDER = new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, BOX_RADII, null));
    private final static Effect BOX_SHADOW_EFFECT = new DropShadow(5, 7, 7, Color.GRAY);
    private final static Background ONLINE_BOX_BACKGROUND  = new Background(new BackgroundFill(ONLINE_BOX_COLOR, BOX_RADII, null));
    private final static Background ONLINE_PEOPLE_BACKGROUND  = new Background(new BackgroundFill(ONLINE_BOX_COLOR.brighter(), null, null));
    private final static Background OFFLINE_BOX_BACKGROUND = new Background(new BackgroundFill(OFFLINE_BOX_COLOR, BOX_RADII, null));
    private final static Background OFFLINE_PEOPLE_BACKGROUND  = new Background(new BackgroundFill(OFFLINE_BOX_COLOR.brighter(), null, null));
    private final static Background DRAG_BOX_BACKGROUND = new Background(new BackgroundFill(DRAG_BOX_COLOR, BOX_RADII, null));

    private static final DataFormat dndDataFormat = DataFormat.PLAIN_TEXT; // Using standard plain text format to ensure drag & drop works between applications

    private VisualGrid peopleBoxFocus;

    class ResourceBoxController {

        final Pane resourcesBoxContainer;
        final Site site;
        private final Label label = new Label();
        private final VisualGrid peopleBox = new SkinnedVisualGrid();
        private final VBox resourceBox = new VBox(LayoutUtil.setMaxWidthToInfinite(label), peopleBox);
        private final ObjectProperty<Entity> resourceConfigurationProperty = new SimpleObjectProperty<>();
        private final ReactiveVisualFilter<DocumentLine> peopleFilter;
        private boolean dragBackgroundVisible;

        ResourceBoxController(Entity resourceConfiguration, Site site, Pane resourcesBoxContainer, ReactiveEntityFilter<?> parentFilter) {
            this.resourcesBoxContainer = resourcesBoxContainer;
            this.site = site;
            setResourceConfiguration(resourceConfiguration);
            label.setTextFill(Color.WHITE);
            label.setAlignment(Pos.CENTER);
            peopleBox.setHeaderVisible(false);
            peopleBox.setFullHeight(true);
            VBox.setMargin(label, new Insets(2));
            VBox.setMargin(peopleBox, new Insets(0, 5, 5, 5));
            VBox.setVgrow(peopleBox, Priority.ALWAYS);
            resourceBox.setMinWidth(150);
            resourceBox.setEffect(BOX_SHADOW_EFFECT);
            peopleFilter = RoomsGraphicActivity.this.<DocumentLine>createReactiveVisualFilter(parentFilter, "{class: 'DocumentLine', columns: 'document.<ident>', where: `!cancelled`, orderBy: 'id'}")
                    .combineIfNotNullOtherwiseForceEmptyResult(resourceConfigurationProperty, rc -> where("resourceConfiguration=?", rc.getPrimaryKey()))
                    // Always loading the fields required for viewing the booking details
                    .combine("{fields: `document.(" + BookingDetailsPanel.REQUIRED_FIELDS + ")`}")
                    .visualizeResultInto(peopleBox.visualResultProperty())
                    .applyDomainModelRowStyle()
                    .setSelectedEntityHandler(peopleBox.visualSelectionProperty(), dl -> {
                        if (dl != null) {
                            setSelectedDocument(dl.getDocument());
                            if (peopleBox != peopleBoxFocus && peopleBoxFocus != null) {
                                VisualGrid previousFocus = peopleBoxFocus;
                                peopleBoxFocus = null;
                                previousFocus.setVisualSelection(null);
                            }
                            peopleBoxFocus = peopleBox;
                        } else if (peopleBox == peopleBoxFocus)
                            setSelectedDocument(null);
                    })
                    .setPush(true)
                    .start();
            update(resourceConfiguration);
            setUpContextMenu(resourceBox, this::createContextMenuActionGroup);
            setUpDragAndDropSupport();
        }

        void setResourceConfiguration(Entity resourceConfiguration) {
            resourceConfigurationProperty.set(resourceConfiguration);
        }

        Entity getResourceConfiguration() {
            return resourceConfigurationProperty.get();
        }

        Node getNode() {
            return resourceBox;
        }

        void update(Entity resourceConfiguration) {
            setResourceConfiguration(resourceConfiguration);
            label.setText((String) resourceConfiguration.evaluate("name"));
            Boolean online = resourceConfiguration.getBooleanFieldValue("online");
            resourceBox.setBackground(dragBackgroundVisible ? DRAG_BOX_BACKGROUND : online ? ONLINE_BOX_BACKGROUND : OFFLINE_BOX_BACKGROUND);
            resourceBox.setBorder(BOX_BORDER);
            peopleBox.setBackground(online ? ONLINE_PEOPLE_BACKGROUND : OFFLINE_PEOPLE_BACKGROUND);
        }

        void delete() {
            peopleFilter.stop();
        }

        void showDragBackground(boolean show) {
            dragBackgroundVisible = show;
            update(getResourceConfiguration());
        }

        ActionGroup createContextMenuActionGroup() {
            return newActionGroup(
                    newOperationAction(() -> new EditResourceConfigurationPropertiesRequest(getResourceConfiguration(), container)),
                    newOperationAction(() -> new ToggleResourceConfigurationOnlineOfflineRequest(getResourceConfiguration())),
                    newOperationAction(() -> new ChangeResourceConfigurationItemRequest(getResourceConfiguration(), container, "acco", site.getId())),
                    newOperationAction(() -> new DeleteResourceRequest(getResourceConfiguration(), container))
            );
        }

        /******************** Drag & Drop support ********************/

        private void setUpDragAndDropSupport() {
            peopleBox.setOnDragDetected(e -> {
                Dragboard db = peopleBox.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.put(dndDataFormat, exportDragSelectionForDragboard());
                db.setContent(content);
            });
            // Showing drag background only when entered and accepted
            resourceBox.setOnDragEntered(e -> showDragBackground(acceptsDrag(e)));
            resourceBox.setOnDragOver(e -> {
                if (acceptsDrag(e))
                    e.acceptTransferModes(TransferMode.MOVE);
            });
            resourceBox.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();
                if (db.hasContent(dndDataFormat)) {
                    Object[] keys = importDragSelectionFromDragboard(db.getContent(dndDataFormat));
                    executeOperation(new MoveToResourceConfigurationRequest(getResourceConfiguration(), keys));
                }
            });
            resourceBox.setOnDragExited(e -> showDragBackground(false));
        }

        private boolean acceptsDrag(DragEvent e) {
            return e.getGestureSource() != peopleBox && e.getDragboard().hasContent(dndDataFormat);
        }

        private String exportDragSelectionForDragboard() {
            // Taking the selected people as drag selection (if set)
            List<DocumentLine> documentLines = peopleFilter.getSelectedEntities();
            if (documentLines == null) // Otherwise (if nobody is selected),
                documentLines = peopleFilter.getCurrentEntityList(); // taking all people
            // Exporting the document lines primary keys as a json array
            return SerialCodecManager.encodePrimitiveArrayToJsonArray(documentLines.stream().map(Entity::getPrimaryKey).toArray()).toJsonString();
        }

        private Object[] importDragSelectionFromDragboard(Object dragContent) {
            // Parsing the expected json array and decoding it as a java array containing all primary keys
            return SerialCodecManager.decodePrimitiveArrayFromJsonArray(Json.parseArray(dragContent.toString()));
        }
    }
}
