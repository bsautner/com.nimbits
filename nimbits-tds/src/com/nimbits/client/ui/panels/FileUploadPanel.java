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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.form.FormPanel.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.service.blob.*;
import com.nimbits.client.service.user.*;
import com.nimbits.client.ui.controls.*;
import com.nimbits.client.ui.helper.*;

import java.util.*;

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
        loginService.login(GWT.getHostPageBaseURL(),
                new LoginInfoAsyncCallback(emailAddressHiddenField));

        final HiddenField<EntityType> uploadTypeHiddenField = new HiddenField<EntityType>();
        uploadTypeHiddenField.setName(Parameters.uploadTypeHiddenField.getText());




        panel.add(uploadTypeHiddenField);

        if (entity != null && entity.getEntityType().equals(EntityType.file)) {
            final HiddenField<String> diagramId = new HiddenField<String>();
            diagramId.setName(Parameters.fileId.getText());
            diagramId.setValue(entity.getKey());
            panel.add(diagramId);
            uploadTypeHiddenField.setValue(entity.getEntityType());
        }


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

    private class LoginInfoAsyncCallback implements AsyncCallback<LoginInfo> {
        private final HiddenField<String> emailAddressHiddenField;

        LoginInfoAsyncCallback(HiddenField<String> emailAddressHiddenField) {
            this.emailAddressHiddenField = emailAddressHiddenField;
        }

        @Override
        public void onFailure(Throwable error) {

        }

        @Override
        public void onSuccess(LoginInfo result) {
            try {
                email = result.getEmailAddress();
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
            try {
                notifyFileAddedListener();
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }

        }
    }
}