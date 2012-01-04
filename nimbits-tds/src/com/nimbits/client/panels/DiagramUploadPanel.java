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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.UploadType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.LoginInfo;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.service.LoginService;
import com.nimbits.client.service.LoginServiceAsync;
import com.nimbits.client.service.diagram.DiagramService;
import com.nimbits.client.service.diagram.DiagramServiceAsync;

import java.util.ArrayList;
import java.util.List;

import static com.nimbits.shared.Utils.isEmptyString;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:28 PM
 */
public class DiagramUploadPanel extends LayoutContainer {

    private final List<DiagramAddedListener> DiagramAddedListeners = new ArrayList<DiagramAddedListener>();
    private Diagram diagram;
    private EmailAddress email;

    public DiagramUploadPanel(UploadType uploadType) {
        this.uploadType = uploadType;
    }

    public DiagramUploadPanel(UploadType uploadType, Diagram diagram) {
        this.uploadType = uploadType;
        this.diagram = diagram;

    }


    private final UploadType uploadType;


    public interface DiagramAddedListener {
        void onDiagramAdded() throws NimbitsException;

    }

    public void addDiagramAddedListeners(final DiagramAddedListener listener) {
        DiagramAddedListeners.add(listener);
    }

    void notifyDiagramAddedListener() throws NimbitsException {
        for (DiagramAddedListener DiagramAddedListener : DiagramAddedListeners) {
            DiagramAddedListener.onDiagramAdded();
        }
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setStyleAttribute("margin", "10px");



        final FormPanel panel = new FormPanel();
        panel.addListener(Events.Submit, new Listener<FormEvent>() {


            @Override
            public void handleEvent(FormEvent formEvent) {
                try {
                    notifyDiagramAddedListener();
                } catch (NimbitsException ignored) {

                }
            }
        });
        panel.setHeaderVisible(false);
        panel.setFrame(false);

        DiagramServiceAsync diagramService = GWT.create(DiagramService.class);
        //  diagramService.getBlobStoreUrl("http://" + Window.Location.getHost() +  "/service/diagram", new AsyncCallback<String>() {
        diagramService.getBlobStoreUrl("/service/diagram", new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable throwable) {
                GWT.log(throwable.getMessage());
            }

            @Override
            public void onSuccess(String s) {
                panel.setAction(s);
            }
        });

        panel.setAction("http://" + Window.Location.getHost() + "/service/diagram");
        panel.setEncoding(Encoding.MULTIPART);
        panel.setMethod(Method.POST);
        //  panel.setButtonAlign(HorizontalAlignment.CENTER);
        panel.setWidth(350);

        final TextField<String> name = new TextField<String>();

        name.setFieldLabel(Const.WORD_NAME);
        name.setName(Const.PARAM_NAME);
        panel.add(name);


        final FileUploadField file = new FileUploadField();
        file.setAllowBlank(false);
        file.setName("myFile");
        file.setFieldLabel("File");
        panel.add(file);

        final HiddenField<String> emailAddressHiddenField=new HiddenField<String>();
        LoginServiceAsync loginService = GWT.create(LoginService.class);
        emailAddressHiddenField.setName(Const.PARAM_EMAIL_HIDDEN_FIELD);

        panel.add(emailAddressHiddenField);
        try {
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
        } catch (NimbitsException e) {
            GWT.log(e.getMessage());
        }

        final HiddenField<UploadType> uploadTypeHiddenField = new HiddenField<UploadType>();
        uploadTypeHiddenField.setName(Const.PARAM_UPLOAD_TYPE_HIDDEN_FIELD);
        uploadTypeHiddenField.setValue(uploadType);
        panel.add(uploadTypeHiddenField);
        if (uploadType == UploadType.updatedFile && diagram != null) {
            final HiddenField<Long> diagramId = new HiddenField<Long>();
            diagramId.setName(Const.PARAM_DIAGRAM_ID);
            diagramId.setValue(diagram.getId());
            panel.add(diagramId);
            name.setValue(diagram.getName().getValue());
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
                // normally would submit the form but for example no server set up to
                // handle the post
                if (!isEmptyString(name.getValue()) && file.getValue().toLowerCase().endsWith(".svg")) {
                    panel.submit();

                }


                //
                // notifyDiagramAddedListener();

            }
        });
        panel.addButton(submitBtn);

        add(panel);
    }

}