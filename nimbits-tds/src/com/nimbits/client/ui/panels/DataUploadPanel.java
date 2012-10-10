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

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.blob.BlobService;
import com.nimbits.client.service.blob.BlobServiceAsync;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.user.UserServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:28 PM
 */
public class DataUploadPanel extends LayoutContainer {
    private static final int WIDTH = 350;
    private static final String RESET = "Reset";
    private static final String SUBMIT = "Submit";
    private static final String MY_FILE = "myFile";
    private static final String FILE = "File";
    private final Collection<FileAddedListener> FileAddedListeners = new ArrayList<FileAddedListener>(1);
    private EmailAddress email;
    private Entity entity;


    public DataUploadPanel(Entity entity) {

        this.entity = entity;
    }
    public DataUploadPanel() {


    }

    public interface FileAddedListener {
        void onFileAdded() throws NimbitsException;

    }

    public void addFileAddedListeners(final FileAddedListener listener) {
        FileAddedListeners.add(listener);
    }

    void notifyFileAddedListener() throws NimbitsException {
        for (FileAddedListener FileAddedListener : FileAddedListeners) {
            FileAddedListener.onFileAdded();
        }
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setStyleAttribute("margin", "10px");
        final FormPanel panel = new FormPanel();


        panel.addListener(Events.Submit, new SubmitFormEventListener());

        BlobServiceAsync service = GWT.create(BlobService.class);
        //  diagramService.getBlobStoreUrl("http://" + Window.Location.getHost() +  "/service/diagram", new AsyncCallback<String>() {
        service.getBlobStoreUrl(Path.PATH_BLOB_SERVICE, new GetBlobStoreURLAsyncCallback(panel));

        panel.setEncoding(Encoding.MULTIPART);
        panel.setMethod(Method.POST);
        panel.setWidth(WIDTH);
        panel.setHeaderVisible(false);
        panel.setBorders(false);

        Html html = new Html("<p>You can upload a comma delimited, 6 column, data file directly to this point. " +
                "The file should use timestamps in unix epoch millisecond format. For a sample file, try " +
                "right clicking on an existing point and using the dump data option do download its data. Your " +
                "uploaded file should be in the same format:</p> " +
                "<p> <strong>timestamp, float value, note, data, gps latitude, gps longitude </strong></p>" +
                "" +
                "<br><br>");

        panel.add(html);
        final FileUploadField file = new FileUploadField();

        file.setAllowBlank(false);
        file.setName(MY_FILE);
        file.setFieldLabel(FILE);
        panel.add(file);



        final HiddenField<String> emailAddressHiddenField=new HiddenField<String>();
        emailAddressHiddenField.setName(Parameters.emailHiddenField.getText());
        panel.add(emailAddressHiddenField);

        final HiddenField<String> fileNameHiddenField=new HiddenField<String>();
        fileNameHiddenField.setName(Parameters.fileName.getText());
        panel.add(fileNameHiddenField);
        UserServiceAsync loginService = GWT.create(UserService.class);
        loginService.login(GWT.getHostPageBaseURL(),
                new LoginInfoAsyncCallback(emailAddressHiddenField));

        final HiddenField<EntityType> uploadTypeHiddenField = new HiddenField<EntityType>();
        uploadTypeHiddenField.setName(Parameters.uploadTypeHiddenField.getText());




        panel.add(uploadTypeHiddenField);

        if (entity != null && entity.getEntityType().equals(EntityType.point)) {
            final HiddenField<String> diagramId = new HiddenField<String>();
            diagramId.setName(Parameters.fileId.getText());
            diagramId.setValue(entity.getKey());
            panel.add(diagramId);
            uploadTypeHiddenField.setValue(entity.getEntityType());
        }


        final Button btn = new Button(RESET);
        btn.addSelectionListener(new ResetButtonEventSelectionListener(panel));
        panel.addButton(btn);

        final Button submitBtn = new Button(SUBMIT);
        submitBtn.addSelectionListener(new SubmitButtonEventSelectionListener(panel, fileNameHiddenField, file));
        panel.addButton(submitBtn);

        add(panel);
    }

    private static class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final FormPanel panel;
        private final HiddenField<String> fileNameHiddenField;
        private final FileUploadField file;

        SubmitButtonEventSelectionListener(FormPanel panel, HiddenField<String> fileNameHiddenField, FileUploadField file) {
            this.panel = panel;
            this.fileNameHiddenField = fileNameHiddenField;
            this.file = file;

        }

        @Override
        public void componentSelected(ButtonEvent ce) {
            if (!panel.isValid()) {
                return;
            }

            fileNameHiddenField.setValue(file.getValue());
            panel.submit();



        }
    }

    private static class GetBlobStoreURLAsyncCallback implements AsyncCallback<String> {
        private final FormPanel panel;

        GetBlobStoreURLAsyncCallback(FormPanel panel) {
            this.panel = panel;
        }

        @Override
        public void onFailure(Throwable throwable) {
            GWT.log(throwable.getMessage());
        }

        @Override
        public void onSuccess(String s) {
            panel.setAction(s);
        }
    }

    private static class ResetButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final FormPanel panel;

        private ResetButtonEventSelectionListener(FormPanel panel) {
            this.panel = panel;
        }

        @Override
        public void componentSelected(ButtonEvent ce) {
            panel.reset();
        }
    }

    private class LoginInfoAsyncCallback implements AsyncCallback<User> {
        private final HiddenField<String> emailAddressHiddenField;

        LoginInfoAsyncCallback(HiddenField<String> emailAddressHiddenField) {
            this.emailAddressHiddenField = emailAddressHiddenField;
        }

        @Override
        public void onFailure(Throwable error) {

        }

        @Override
        public void onSuccess(User result) {
            try {
                email = result.getEmail();
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);

                   }
            emailAddressHiddenField.setValue(email.getValue());
        }

    }

    private class SubmitFormEventListener implements Listener<FormEvent> {
        SubmitFormEventListener() {
        }

        @Override
            public void handleEvent(FormEvent formEvent) {

                Window.alert("A background task has started to process your data. You can close this window or upload another file.");


        }
    }
}