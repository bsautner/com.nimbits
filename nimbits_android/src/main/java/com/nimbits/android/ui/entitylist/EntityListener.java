package com.nimbits.android.ui.entitylist;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;
import com.nimbits.cloudplatform.client.model.value.Value;

/**
 * Created by benjamin on 7/24/13.
 */
public interface EntityListener {
    void onEntityClicked(final Entity entity);
    void onNewEntity(final Entity parent, final EntityType type, final EntityName name);
    void onValueUpdated(final Entity entity, final Value response);
    void onNewValue(final Entity entity, final String entry);
    void newValuePrompt(final Entity entity);
}
