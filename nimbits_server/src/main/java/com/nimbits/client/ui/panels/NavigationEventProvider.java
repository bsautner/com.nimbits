/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;

import java.util.ArrayList;
import java.util.Collection;

public abstract class NavigationEventProvider extends LayoutContainer {

    private final Collection<EntityDeletedListener> entityDeletedListeners = new ArrayList<EntityDeletedListener>(1);

    private final Collection<EntityClickedListener> entityClickedListeners = new ArrayList<EntityClickedListener>(1);

    private final Collection<ValueEnteredListener> valueEnteredListeners = new ArrayList<ValueEnteredListener>(1);

    private final Collection<EntityAddedListener> entityAddedListeners = new ArrayList<EntityAddedListener>(1);


    public interface EntityAddedListener {
        void onEntityAdded(final Entity entity);
    }

    public void addEntityAddedListener(final EntityAddedListener listener) {
        entityAddedListeners.add(listener);
    }


    void notifyEntityAddedListener(final Entity model) {
        for (EntityAddedListener listener : entityAddedListeners) {
            listener.onEntityAdded(model);
        }
    }


    public void addEntityDeletedListeners(final EntityDeletedListener listener) {
        entityDeletedListeners.add(listener);
    }

    void notifyEntityDeletedListener(final Entity entity) {
        for (EntityDeletedListener listener : entityDeletedListeners) {
            listener.onEntityDeleted(entity);
        }
    }

    public interface EntityDeletedListener {
        void onEntityDeleted(final Entity entity);

    }

    // Value entered Handlers
    public interface ValueEnteredListener {
        void onValueEntered(final TreeModel model, final Value value);

    }

    void addValueEnteredListeners(final ValueEnteredListener listener) {
        valueEnteredListeners.add(listener);
    }

    void notifyValueEnteredListener(final TreeModel model, final Value value) {
        for (final ValueEnteredListener valueEnteredListener : valueEnteredListeners) {
            valueEnteredListener.onValueEntered(model, value);
        }
    }

    // Point Click Handlers
    public interface EntityClickedListener {
        void onEntityClicked(final TreeModel entity);

    }

    void addEntityClickedListeners(final EntityClickedListener listener) {
        entityClickedListeners.add(listener);
    }

    void notifyEntityClickedListener(final TreeModel entity) {

        for (EntityClickedListener clickedListener : entityClickedListeners) {
            clickedListener.onEntityClicked(entity);
        }
    }


}
