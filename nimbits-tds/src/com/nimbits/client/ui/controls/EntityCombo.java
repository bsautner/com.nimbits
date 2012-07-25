/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.ui.helper.*;

import java.util.*;

public class EntityCombo extends ComboBox<TreeModel> {


    public EntityCombo(final EntityType type,
                       final String selectedUUID,
                       final String emptyText) {

        final ListStore<TreeModel> cbStore = new ListStore<TreeModel>();
        final EntityServiceAsync service = GWT.create(EntityService.class);
        setEmptyText(emptyText);
        setStore(cbStore);
        setDisplayField(Parameters.name.getText());
        setValueField(Parameters.id.getText());
        setEditable(true);
        setAutoValidate(true);



        service.getEntityMap(type, 100, new GetEntityMapAsyncCallback(emptyText, cbStore, selectedUUID));




    }

    private class GetEntityMapAsyncCallback implements AsyncCallback<Map<String, Entity>> {
        private final String emptyText;
        private final ListStore<TreeModel> cbStore;
        private final String selectedUUID;

        GetEntityMapAsyncCallback(String emptyText, ListStore<TreeModel> cbStore, String selectedUUID) {
            this.emptyText = emptyText;
            this.cbStore = cbStore;
            this.selectedUUID = selectedUUID;
        }

        @Override
        public void onFailure(final Throwable caught) {
            GWT.log(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(final Map<String, Entity> result) {
            setEmptyText(emptyText);
            for (final Entity e : result.values()) {

                try {
                    TreeModel model = new GxtModel(e);
                    cbStore.add(model);
                    if (model.getBaseEntity().getKey().equals(selectedUUID)) {
                        setValue(model);
                    }
                } catch (NimbitsException e1) {
                    FeedbackHelper.showError(e1);
                }


            }
        }
    }
}
