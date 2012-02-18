/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;

import java.util.Map;

public class EntityCombo extends ComboBox<GxtModel> {

//    public String getText() {
//        List<GxtModel> s = getSelection();
//        if (s.size() > 0) {
//            GxtModel si = s.get(0);
//            return si.getName().getValue();
//        } else {
//            return null;
//
//        }
//
//    }
//

    public EntityCombo(final EntityType type, final String selectedUUID) {
        setEmptyText(Const.MESSAGE_LOADING_POINTS);
        final ListStore<GxtModel> cbStore = new ListStore<GxtModel>();
        setStore(cbStore);
        setDisplayField(Const.PARAM_NAME);
        setValueField(Const.PARAM_ID);
        setEditable(true);
        setAutoValidate(true);

        EntityServiceAsync service = GWT.create(EntityService.class);

        service.getEntityMap(type, new AsyncCallback<Map<String, Entity>>() {
            @Override
            public void onFailure(Throwable caught) {
                //auto generated
            }

            @Override
            public void onSuccess(Map<String, Entity> result) {
                setEmptyText(Const.MESSAGE_SELECT_POINT);

                for (Entity e : result.values()) {
                    GxtModel model = new GxtModel(e);
                    cbStore.add(model);
                    if (model.getBaseEntity().getEntity().equals(selectedUUID)) {
                       setValue(model);
                    }

                }
            }
        });




    }
}
