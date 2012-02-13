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

import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.datapoints.*;

import java.util.*;

public class PointCombo extends ComboBox<GxtModel> {

    public String getText() {
        List<GxtModel> s = getSelection();
        if (s.size() > 0) {
            GxtModel si = s.get(0);
            return si.getName().getValue();
        } else {
            return null;

        }

    }

    public GxtModel getPoint() {
        List<GxtModel> s = getSelection();
        return s.get(0);

    }

    public PointCombo(final User user ) {
        setEmptyText(Const.MESSAGE_LOADING_POINTS);
        final ListStore<GxtModel> cbStore = new ListStore<GxtModel>();
        setStore(cbStore);
        setDisplayField(Const.PARAM_NAME);
        setValueField(Const.PARAM_ID);
        setEditable(false);

        PointServiceAsync pointService = GWT.create(PointService.class);

        pointService.getPoints(new AsyncCallback<List<Point>>() {

            @Override
            public void onFailure(Throwable caught) {

                GWT.log(caught.getMessage());
            }

            @Override
            public void onSuccess(List<Point> result) {

                setEmptyText(Const.MESSAGE_SELECT_POINT);

                for (Point p : result) {
                    GxtModel model = new GxtModel(user, p);
                    cbStore.add(model);

                }

            }

        });

    }
}
