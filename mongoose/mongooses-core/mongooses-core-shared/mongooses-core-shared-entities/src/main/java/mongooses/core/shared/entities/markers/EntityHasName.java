package mongooses.core.shared.entities.markers;

import webfx.framework.orm.entity.Entity;

/**
 * @author Bruno Salmon
 */
public interface EntityHasName extends Entity, HasName {

    @Override
    default void setName(String name) {
        setFieldValue("name", name);
    }

    @Override
    default String getName() {
        return getStringFieldValue("name");
    }
}