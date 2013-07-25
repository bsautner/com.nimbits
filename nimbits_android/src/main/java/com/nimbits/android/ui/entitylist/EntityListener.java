package com.nimbits.android.ui.entitylist;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityName;

/**
 * Created by benjamin on 7/24/13.
 */
public interface EntityListener {
      void onEntityClicked(final Entity entity);
      void onNewEntity(Entity parent, EntityType type, EntityName name);
}
