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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.client.service.value.ValueServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;

import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.user.client.Window.alert;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class DownloadPanel extends NavigationEventProvider {


    private static final int WIDTH = 350;
    private FormData formdata;
    private VerticalPanel vp;

    private final Entity entity;

    public DownloadPanel(final Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);


        ValueServiceAsync service = GWT.create(ValueService.class);

        service.getAllStores(entity, new AsyncCallback<List<ValueBlobStore>>() {
            @Override
            public void onFailure(Throwable caught) {
              FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(List<ValueBlobStore> result) {
                alert("" + result.size());
            }
        });


//        try {
////            createForm();
////            add(vp);
////            doLayout();
//        } catch (NimbitsException e) {
//            FeedbackHelper.showError(e);
//        }



    }




    private void createForm() throws NimbitsException {


    }







}


