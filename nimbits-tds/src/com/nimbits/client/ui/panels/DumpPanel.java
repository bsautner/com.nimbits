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
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
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

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class DumpPanel extends LayoutContainer {
    private TextField<String> endDateSelector;
    private TextField<String> startDateSelector;


    private static final int WIDTH = 350;

    private  Timespan timespan;

    private FormData formdata;
    private VerticalPanel vp;
    private Entity entity;

    public DumpPanel(final Entity entity) {
        this.entity = entity;



    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        setLayout(new FillLayout());
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);


        try {
            createForm();
        } catch (NimbitsException e) {
          FeedbackHelper.showError(e);
        }
        add(vp);
            doLayout();




    }




    private void createForm() throws NimbitsException {

        final FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);

        Html html = new Html("You can schedule a job to start that will dump all of the data for this point to a flat file. " +
                "Nimbits will email you when the file is complete, with a hyperlink for you to download.");


        endDateSelector = new TextField<String>();
        startDateSelector =new TextField<String>();

        timespan = TimespanModelFactory.createTimespan(new Date(new Date().getTime() - 80000), new Date());


        DateTimeFormat fmt = DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME);
        startDateSelector.setValue(fmt.format(this.timespan.getStart()));
        startDateSelector.setFieldLabel("Start Date");
        endDateSelector.setValue(fmt.format(this.timespan.getEnd()));
        endDateSelector.setFieldLabel("End Date");

        simple.add(html, formdata);
        simple.add(startDateSelector, formdata);
        simple.add(endDateSelector, formdata);




        Button submit = new Button("Submit");

        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {

                try {
                    createDoc();
                } catch (NimbitsException e) {
                    FeedbackHelper.showError(e);
                }

            }
        });

        submit.setWidth(200);
        simple.addButton(submit);

        vp.add(simple);
    }

    private void createDoc() throws NimbitsException {
        final MessageBox box = new MessageBox().wait("Exporting to Drive", "Scheduling Task Please Wait", "Dumping Data");
        box.show();
        final ValueServiceAsync service = GWT.create(ValueService.class);
        timespan = TimespanServiceClientImpl.createTimespan(startDateSelector.getValue(), endDateSelector.getValue());

        service.createDataDump(entity, timespan, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                box.close();
                FeedbackHelper.showError(caught);

            }

            @Override
            public void onSuccess(Void result) {
                box.close();
                Window.alert("A background task has started to process your request. An email will be sent to you soon with a link to download your data. You can close this window or upload another file.");

            }
        });

    }










    }







