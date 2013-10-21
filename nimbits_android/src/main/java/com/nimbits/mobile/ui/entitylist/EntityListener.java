/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.mobile.ui.entitylist;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.value.Value;

/**
 * Created by benjamin on 7/24/13.
 */
public interface EntityListener {
    void onEntityClicked(final Entity entity, final boolean checkChildren);

    void onNewEntity(final Entity parent, final EntityType type, final EntityName name);

    void onValueUpdated(final Entity entity, final Value response);

    void onNewValue(final Entity entity, final String entry);

    void newValuePrompt(final Entity entity);



}
