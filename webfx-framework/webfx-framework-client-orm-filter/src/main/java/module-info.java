// Generated by WebFx

module webfx.framework.client.orm.filter {

    // Direct dependencies modules
    requires java.base;
    requires javafx.base;
    requires javafx.graphics;
    requires webfx.extras.type;
    requires webfx.framework.client.activity;
    requires webfx.framework.client.i18n;
    requires webfx.framework.client.orm.dql;
    requires webfx.framework.client.push;
    requires webfx.framework.shared.orm.domain;
    requires webfx.framework.shared.orm.entity;
    requires webfx.framework.shared.orm.expression;
    requires webfx.framework.shared.querypush;
    requires webfx.framework.shared.util;
    requires webfx.kit.util;
    requires webfx.platform.client.uischeduler;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.query;
    requires webfx.platform.shared.scheduler;
    requires webfx.platform.shared.util;

    // Exported packages
    exports webfx.framework.client.orm.entity.filter;
    exports webfx.framework.client.orm.entity.filter.reactive_call;
    exports webfx.framework.client.orm.entity.filter.reactive_call.query;
    exports webfx.framework.client.orm.entity.filter.reactive_call.querypush;
    exports webfx.framework.client.orm.entity.filter.table;
    exports webfx.framework.client.orm.mapping.entity_to_object;

}