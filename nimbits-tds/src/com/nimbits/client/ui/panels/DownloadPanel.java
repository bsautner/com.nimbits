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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.constants.Const;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.timespan.Timespan;
import com.nimbits.client.model.timespan.TimespanModelFactory;
import com.nimbits.client.model.timespan.TimespanServiceClientImpl;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.client.service.value.ValueServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.Date;

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

                    createDoc();

            }
        });


        panel.add(submit, formdata);

        vp.add(panel);
    }

    private void createDoc() {
        final MessageBox box = new MessageBox().wait("Exporting to Drive", "Please wait", "Creating Spreadsheet");
        box.show();
        final ValueServiceAsync service = GWT.create(ValueService.class);
        service.createGoogleDoc(entity, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                box.close();
              FeedbackHelper.showError(caught);

            }

            @Override
            public void onSuccess(Void result) {
               box.close();
               preloadData();
            }
        });

    }

    private void settingHeader(final int total) {
        final MessageBox box = new MessageBox().wait("Exporting to Drive", "Please wait", "Setting Headers");
        box.show();
        final ValueServiceAsync service = GWT.create(ValueService.class);
        service.addSpreadsheetHeader(entity, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                box.close();
                FeedbackHelper.showError(caught);

            }

            @Override
            public void onSuccess(Void result) {
                box.close();
                export(total);
            }
        });

    }


    private void preloadData() {
        final MessageBox box = new MessageBox().wait("Exporting to Drive", "Please wait", "Preparing Data");
        box.show();
        try {
            timespan = TimespanServiceClientImpl.createTimespan(startDateSelector.getValue().toString(), endDateSelector.getValue().toString());


        final ValueServiceAsync service = GWT.create(ValueService.class);
        service.preloadTimespan(entity,timespan, new AsyncCallback<Integer>() {

            @Override
            public void onFailure(Throwable caught) {
                box.close();
              FeedbackHelper.showError(caught);

            }

            @Override
            public void onSuccess(Integer result) {
                box.close();
                resizeSpreadsheet(result);
            }
        });
        } catch (NimbitsException e) {
            FeedbackHelper.showError(e);
        }

    }


    private void resizeSpreadsheet(final int size) {
        final MessageBox box = new MessageBox().wait("Exporting to Drive", "Please wait", "Setting spreadsheet size");
        box.show();



            final ValueServiceAsync service = GWT.create(ValueService.class);
            service.setSpreadsheetSize(entity, size, new AsyncCallback<Void>() {

                @Override
                public void onFailure(Throwable caught) {
                    box.close();
                    FeedbackHelper.showError(caught);

                }

                @Override
                public void onSuccess(Void result) {
                    box.close();
                    settingHeader(size);
                }


            });


    }

    private void export(int total) {
        final MessageBox box = new MessageBox().wait("Exporting to Drive", "Please wait", "Dumping " + total  + " values");
        final ValueServiceAsync service = GWT.create(ValueService.class);
        service.startGoogleDocExport(entity, total, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                box.close();
                FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(Void result) {
                box.close();
            }
        });
    }







}


