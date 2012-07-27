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
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.accesskey.AccessKeyFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.model.timespan.TimespanServiceClientImpl;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.client.service.value.ValueServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;
import com.nimbits.server.transactions.service.value.ValueServiceFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.gwt.user.client.Window.alert;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class DownloadPanel extends LayoutContainer {
    private TextField endDateSelector;
    private  TextField startDateSelector;
    private static final int WIDTH = 350;

    private VerticalPanel vp;
    private  Timespan timespan;
    private final Entity entity;
    MessageBox box;


    public DownloadPanel(final Entity entity) {
        this.entity = entity;



    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        setLayout(new FillLayout());

        vp = new VerticalPanel();
        vp.setBorders(false);

        //vp.setSpacing(10);
        createForm();
        add(vp);



    }




    private void createForm()   {
        FormData formdata = new FormData("-20");
        final FormPanel panel = new FormPanel();
        panel.setLayout(new FitLayout());
        panel.setFrame(false);
        panel.setHeaderVisible(false);
        panel.setBodyBorder(false);

        endDateSelector = new TextField();
        startDateSelector = new TextField();
        timespan = TimespanModelFactory.createTimespan(new Date(), new Date());


        DateTimeFormat fmt = DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME);
        startDateSelector.setValue(fmt.format(this.timespan.getStart()));
        startDateSelector.setFieldLabel("Start Date");
        endDateSelector.setValue(fmt.format(this.timespan.getEnd()));
        endDateSelector.setFieldLabel("End Date");


        panel.add(startDateSelector, formdata);
        panel.add(endDateSelector, formdata);




        Button submit = new Button("Submit");

        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                try {
                    run(-2);
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }
            }
        });


        panel.add(submit, formdata);

        vp.add(panel);
    }

    private void run(final int count) throws NimbitsException {
        final ValueServiceAsync service = GWT.create(ValueService.class);
        timespan = TimespanServiceClientImpl.createTimespan(startDateSelector.getValue().toString(), endDateSelector.getValue().toString());

       if (count == -2) {
        box = MessageBox.progress(
                "Exporting to Google Docs", "Exporting to your Google Drive", "Creating spreadsheet");
        box.show();
       }
       else if (count == -1) {
           box.setProgressText("Pre-loading data from storage into cloud memory");

        }
       else {
           box.setProgressText(  "Saving values " + count + " to " + count + 100);

       }

        service.startGoogleDocExport(entity, count, timespan, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
               FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(String result) {

                try {
                    if (result.equals("created")) {

                        run(-1);
                    }
                    else if (result.equals("loaded")) {
                        run(0);
                    }
                    else if (result.equals("found")) {
                        run(count + 100);
                    }
                    else if (result.equals("done")) {
                        box.close();
                    }

                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }

            }
        });


    }





}


