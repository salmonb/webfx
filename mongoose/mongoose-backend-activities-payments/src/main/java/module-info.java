// Generated by WebFx

module mongoose.backend.activities.payments {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires mongoose.backend.masterslave;
    requires mongoose.backend.operations.generic;
    requires mongoose.backend.operations.moneytransfer;
    requires mongoose.client.activity;
    requires mongoose.client.presentationmodel;
    requires mongoose.client.util;
    requires mongoose.shared.domain;
    requires mongoose.shared.entities;
    requires webfx.extras.visual;
    requires webfx.extras.visual.controls.grid;
    requires webfx.framework.client.action;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.domain;
    requires webfx.framework.client.layouts;
    requires webfx.framework.client.orm.dql;
    requires webfx.framework.client.orm.filter.visual;
    requires webfx.framework.client.uirouter;
    requires webfx.framework.shared.operation;
    requires webfx.framework.shared.orm.expression;
    requires webfx.framework.shared.router;
    requires webfx.platform.client.windowhistory;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.backend.activities.payments;
    exports mongoose.backend.activities.payments.routing;
    exports mongoose.backend.operations.routes.payments;

    // Provided services
    provides webfx.framework.client.operations.route.RouteRequestEmitter with mongoose.backend.activities.payments.RouteToPaymentsRequestEmitter;
    provides webfx.framework.client.ui.uirouter.UiRoute with mongoose.backend.activities.payments.PaymentsUiRoute;

}