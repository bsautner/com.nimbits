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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.ui.helper.FeedbackHelper;



public abstract class BasePanel extends NavigationEventProvider {
    public final User user;

    static final String MESSAGE_SELECT_POINT = "Select a Data Point";
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    private static final int FORM_WIDTH = 350;
    private static final String SUBMIT = "Submit";
    private static final String CANCEL = "Cancel";
    final FormData formdata;
    final VerticalPanel vp;
    protected final FormPanel simple;
    private final LayoutContainer controlButtons = new LayoutContainer();
    protected final Button submit = new Button(SUBMIT);
    private final Button cancel = new Button(CANCEL);
    protected final PanelEvent listener;
    private Html helpLink;

    public BasePanel(User user, PanelEvent listener, String helpHtml) {
        this.user = user;
        this.listener = listener;
        formdata = new FormData("-20");
        helpLink = new Html();
        helpLink.setHtml(helpHtml);

        vp = new VerticalPanel();

        vp.setSpacing(15);
        vp.add(helpLink);
        simple = new FormPanel();
        FormPanel simple = new FormPanel();
        simple.setWidth(FORM_WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);

        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(15));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        controlButtons.setLayout(layout);
        cancel.setWidth(100);
        submit.setWidth(100);
        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        controlButtons.add(cancel, layoutData);
        controlButtons.add(submit, layoutData);

        cancel.addSelectionListener(new CancelButtonEventSelectionListener());
    }


    public interface PanelEvent {

        void close();

    }

    protected void completeForm() {
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);

        vp.add(simple);
        vp.add(controlButtons);

        add(vp);
        doLayout();
    }

    protected void close() {
        listener.close();
    }

    private class CancelButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        CancelButtonEventSelectionListener() {
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {

            listener.close();
        }
    }

    protected class AddEntityAsyncCallback implements AsyncCallback<Entity> {
        private final MessageBox box;

        AddEntityAsyncCallback(MessageBox box) {
            this.box = box;
        }

        @Override
        public void onFailure(final Throwable e) {
            GWT.log(e.getMessage(), e);
            box.close();
            FeedbackHelper.showError(e);
            try {
                notifyEntityAddedListener(null);
            } catch (Exception e1) {
                FeedbackHelper.showError(e);
            }
        }

        @Override
        public void onSuccess(final Entity result) {
            box.close();

            try {
                notifyEntityAddedListener(result);
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }
        }
    }


}
