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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.UploadType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.LoginInfo;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.service.LoginService;
import com.nimbits.client.service.LoginServiceAsync;
import com.nimbits.client.service.blob.BlobService;
import com.nimbits.client.service.blob.BlobServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:28 PM
 */
public class FileUploadPanel extends LayoutContainer {
    private final UploadType uploadType;
    private final List<FileAddedListener> FileAddedListeners = new ArrayList<FileAddedListener>();
    private EmailAddress email;
    private Entity entity;

    public FileUploadPanel(UploadType uploadType) {
        this.uploadType = uploadType;
    }

    public FileUploadPanel(UploadType uploadType, Entity entity) {
        this.uploadType = uploadType;
        //this.diagram = diagram;
        this.entity = entity;
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
        panel.setEncoding(FormPanel.Encoding.MULTIPART);
        panel.addListener(Events.Submit, new Listener<FormEvent>() {
        @Override
            public void handleEvent(FormEvent formEvent) {
            try {
                notifyFileAddedListener();
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }

        }
        });
        panel.setHeaderVisible(false);
        panel.setFrame(false);
        BlobServiceAsync service = GWT.create(BlobService.class);
        //  diagramService.getBlobStoreUrl("http://" + Window.Location.getHost() +  "/service/diagram", new AsyncCallback<String>() {
        service.getBlobStoreUrl(Path.PATH_BLOB_SERVICE, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                GWT.log(throwable.getMessage());
            }

            @Override
            public void onSuccess(String s) {
                panel.setAction(s);
            }
        });

        panel.setEncoding(Encoding.MULTIPART);
        panel.setMethod(Method.POST);
        panel.setWidth(350);

        final TextArea name = new TextArea();
        final FileUploadField file = new FileUploadField();

        file.setAllowBlank(false);
        file.setName("myFile");
        file.setFieldLabel("File");
        panel.add(file);
        name.setFieldLabel("Description");
        name.setName(Parameters.description.getText());
        panel.add(name);

        final HiddenField<String> emailAddressHiddenField=new HiddenField<String>();
        emailAddressHiddenField.setName(Parameters.emailHiddenField.getText());
        panel.add(emailAddressHiddenField);

        final HiddenField<String> fileNameHiddenField=new HiddenField<String>();
        fileNameHiddenField.setName(Parameters.fileName.getText());
        panel.add(fileNameHiddenField);
        LoginServiceAsync loginService = GWT.create(LoginService.class);
        loginService.login(GWT.getHostPageBaseURL(),
                new AsyncCallback<LoginInfo>() {
                    @Override
                    public void onFailure(Throwable error) {

                    }

                    @Override
                    public void onSuccess(LoginInfo result) {
                        email = result.getEmailAddress();
                        emailAddressHiddenField.setValue(email.getValue());
                    }

                });

        final HiddenField<UploadType> uploadTypeHiddenField = new HiddenField<UploadType>();
        uploadTypeHiddenField.setName(Parameters.uploadTypeHiddenField.getText());
        uploadTypeHiddenField.setValue(uploadType);
        panel.add(uploadTypeHiddenField);
        if (uploadType == UploadType.updatedFile && entity != null) {
            final HiddenField<String> diagramId = new HiddenField<String>();
            diagramId.setName(Parameters.fileId.getText());
            diagramId.setValue(entity.getKey());
            panel.add(diagramId);
            try {
                name.setValue(entity.getName().getValue());
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
            name.setReadOnly(true);
            name.setVisible(false);
        }


        final Button btn = new Button("Reset");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                panel.reset();
            }
        });
        panel.addButton(btn);

        final Button submitBtn = new Button("Submit");
        submitBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!panel.isValid()) {
                    return;
                }
                fileNameHiddenField.setValue(file.getValue());
                panel.submit();



            }
        });
        panel.addButton(submitBtn);

        add(panel);
    }

}