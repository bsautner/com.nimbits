/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.blob.BlobService;
import com.nimbits.client.service.blob.BlobServiceAsync;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.user.UserServiceAsync;
import com.nimbits.client.ui.controls.EntityPanel;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:28 PM
 */
public class FileUploadPanel extends LayoutContainer {
    private static final int WIDTH = 350;
    private final Collection<FileAddedListener> FileAddedListeners = new ArrayList<FileAddedListener>(1);
    private EmailAddress email;
    private Entity entity;


    public FileUploadPanel(Entity entity) {

        this.entity = entity;
    }
    public FileUploadPanel() {


    }

    public interface FileAddedListener {
        void onFileAdded() ;

    }

    public void addFileAddedListeners(final FileAddedListener listener) {
        FileAddedListeners.add(listener);
    }

    void notifyFileAddedListener()  {
        for (FileAddedListener FileAddedListener : FileAddedListeners) {
            FileAddedListener.onFileAdded();
        }
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setStyleAttribute("margin", "10px");
        final EntityPanel panel = new EntityPanel(entity);


        panel.addListener(Events.Submit, new SubmitFormEventListener());

        BlobServiceAsync service = GWT.create(BlobService.class);
        //  diagramService.getBlobStoreUrl("http://" + Window.Location.getHost() +  "/service/diagram", new AsyncCallback<String>() {
        service.getBlobStoreUrl(Path.PATH_BLOB_SERVICE, new GetBlobStoreURLAsyncCallback(panel));

        panel.setEncoding(Encoding.MULTIPART);
        panel.setMethod(Method.POST);
        panel.setWidth(WIDTH);


        final FileUploadField file = new FileUploadField();

        file.setAllowBlank(false);
        file.setName("myFile");
        file.setFieldLabel("File");
        panel.add(file);



        final HiddenField<String> emailAddressHiddenField=new HiddenField<String>();
        emailAddressHiddenField.setName(Parameters.emailHiddenField.getText());
        panel.add(emailAddressHiddenField);

        final HiddenField<String> fileNameHiddenField=new HiddenField<String>();
        fileNameHiddenField.setName(Parameters.fileName.getText());
        panel.add(fileNameHiddenField);
        UserServiceAsync loginService = GWT.create(UserService.class);
        loginService.loginRpc(GWT.getHostPageBaseURL(),
                new LoginInfoAsyncCallback(emailAddressHiddenField));

        final HiddenField<EntityType> uploadTypeHiddenField = new HiddenField<EntityType>();
        uploadTypeHiddenField.setName(Parameters.uploadTypeHiddenField.getText());




        panel.add(uploadTypeHiddenField);



        final Button btn = new Button("Reset");
        btn.addSelectionListener(new ResetButtonEventSelectionListener(panel));
        panel.addButton(btn);

        final Button submitBtn = new Button("Submit");
        submitBtn.addSelectionListener(new SubmitButtonEventSelectionListener(panel, fileNameHiddenField, file));
        panel.addButton(submitBtn);

        add(panel);
    }

    private static class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        private final EntityPanel panel;
        private final HiddenField<String> fileNameHiddenField;
        private final FileUploadField file;

        SubmitButtonEventSelectionListener(EntityPanel panel, HiddenField<String> fileNameHiddenField, FileUploadField file) {
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
            } catch (Exception e) {
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
            try {
                notifyFileAddedListener();
            } catch (Exception e) {
                FeedbackHelper.showError(e);
            }

        }
    }
}