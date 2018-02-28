package naga.framework.ui.action;

import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import naga.framework.ui.i18n.I18n;
import naga.util.collection.Collections;

import java.util.Collection;

/**
 * @author Bruno Salmon
 */
public class ActionGroupBuilder extends ActionBuilder {

    private Collection<Action> actions;

    public ActionGroupBuilder() {
    }

    public ActionGroupBuilder(Object actionKey) {
        super(actionKey);
    }

    public ActionGroupBuilder setActions(Collection<Action> actions) {
        this.actions = actions;
        return this;
    }

    public ActionGroupBuilder setActions(Action... actions) {
        return setActions(Collections.listOf(actions));
    }

    @Override
    public ActionGroup build() {
        completePropertiesForBuild();
        return ActionGroup.create(actions, getTextProperty(), getGraphicProperty(), getDisabledProperty(), getVisibleProperty(), getActionHandler());
    }

    @Override
    public ActionGroupBuilder setActionKey(Object actionKey) {
        return (ActionGroupBuilder) super.setActionKey(actionKey);
    }

    @Override
    public ActionGroupBuilder setTextProperty(ObservableStringValue textProperty) {
        return (ActionGroupBuilder) super.setTextProperty(textProperty);
    }

    @Override
    public ActionGroupBuilder setText(String text) {
        return (ActionGroupBuilder) super.setText(text);
    }

    @Override
    public ActionGroupBuilder setI18nKey(Object i18nKey) {
        return (ActionGroupBuilder) super.setI18nKey(i18nKey);
    }

    @Override
    public ActionGroupBuilder setI18n(I18n i18n) {
        return (ActionGroupBuilder) super.setI18n(i18n);
    }

    @Override
    public ActionGroupBuilder setGraphicProperty(ObservableObjectValue<Node> graphicProperty) {
        return (ActionGroupBuilder) super.setGraphicProperty(graphicProperty);
    }

    @Override
    public ActionGroupBuilder setGraphic(Node graphic) {
        return (ActionGroupBuilder) super.setGraphic(graphic);
    }

    @Override
    public ActionGroupBuilder setGraphicUrlOrJson(Object graphicUrlOrJson) {
        return (ActionGroupBuilder) super.setGraphicUrlOrJson(graphicUrlOrJson);
    }

    @Override
    public ActionGroupBuilder setDisabledProperty(ObservableBooleanValue disabledProperty) {
        return (ActionGroupBuilder) super.setDisabledProperty(disabledProperty);
    }

    @Override
    public ActionGroupBuilder setVisibleProperty(ObservableBooleanValue visibleProperty) {
        return (ActionGroupBuilder) super.setVisibleProperty(visibleProperty);
    }

    @Override
    public ActionGroupBuilder setHiddenWhenDisabled(boolean hiddenWhenDisabled) {
        return (ActionGroupBuilder) super.setHiddenWhenDisabled(hiddenWhenDisabled);
    }

    @Override
    public ActionGroupBuilder setAuthRequired(boolean authRequired) {
        return (ActionGroupBuilder) super.setAuthRequired(authRequired);
    }

    @Override
    public ActionGroupBuilder setAuthorizedProperty(ObservableBooleanValue authorizedProperty) {
        return (ActionGroupBuilder) super.setAuthorizedProperty(authorizedProperty);
    }

    @Override
    public ActionGroupBuilder setActionHandler(EventHandler<ActionEvent> actionHandler) {
        return (ActionGroupBuilder) super.setActionHandler(actionHandler);
    }

    @Override
    public ActionGroupBuilder setActionHandler(Runnable actionHandler) {
        return (ActionGroupBuilder) super.setActionHandler(actionHandler);
    }

    @Override
    public ActionGroupBuilder register() {
        return (ActionGroupBuilder) super.register();
    }

    @Override
    public ActionGroupBuilder duplicate() {
        return ((ActionGroupBuilder) super.duplicate()).setActions(actions);
    }

    @Override
    ActionGroupBuilder newActionBuilder(Object actionKey) {
        return new ActionGroupBuilder(actionKey);
    }

    @Override
    public ActionGroupBuilder removeText() {
        return (ActionGroupBuilder) super.removeText();
    }
}