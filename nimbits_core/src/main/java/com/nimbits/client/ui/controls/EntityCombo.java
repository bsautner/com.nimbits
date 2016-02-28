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

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.service.entity.EntityServiceRpc;
import com.nimbits.client.service.entity.EntityServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.Map;

public class EntityCombo extends ComboBox<TreeModel> {


    public EntityCombo(final EntityType type,
                       final String selectedUUID,
                       final String emptyText) {

        final ListStore<TreeModel> cbStore = new ListStore<TreeModel>();
        final EntityServiceRpcAsync service = GWT.create(EntityServiceRpc.class);
        setEmptyText(emptyText);
        setStore(cbStore);
        setDisplayField(Parameters.name.getText());
        setValueField(Parameters.id.getText());
        setEditable(true);
        setAutoValidate(true);


        service.getEntityMapRpc(type.getCode(), 100, new GetEntityMapAsyncCallback(emptyText, cbStore, selectedUUID));


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
            FeedbackHelper.showError(caught);
        }

        @Override
        public void onSuccess(final Map<String, Entity> result) {
            setEmptyText(emptyText);
            for (final Entity e : result.values()) {

                try {
                    if (((e.getEntityType().equals(EntityType.point)))
                            || !(e.getEntityType().equals(EntityType.point))) {
                        TreeModel model = new GxtModel(e);
                        cbStore.add(model);
                        if (model.getBaseEntity().getKey().equals(selectedUUID)) {
                            setValue(model);
                        }

                    }

                } catch (Exception e1) {
                    FeedbackHelper.showError(e1);
                }


            }
        }
    }
}
