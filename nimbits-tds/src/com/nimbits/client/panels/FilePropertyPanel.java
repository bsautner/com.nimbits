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

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.controls.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.service.blob.*;
import com.nimbits.client.service.entity.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/31/11
 * Time: 1:05 PM
 */
public class FilePropertyPanel extends NavigationEventProvider {

    //private final Diagram diagram;
    private final Entity entity;


    private final ProtectionLevelOptions protectionLevelOptions;



    public FilePropertyPanel(final Entity entity) {
        this.entity = entity;
        this.protectionLevelOptions = new ProtectionLevelOptions(entity);
      //  this.diagram = d;
      //  this.readOnly = entity.isReadOnly();
    }

    private VerticalPanel vp;

    private FormData formData;

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        formData = new FormData("-20");
        vp = new VerticalPanel();
        ToolBar mainToolBar = mainToolBar();
        vp.add(mainToolBar);
        vp.setSpacing(10);
        createForm();

        add(vp);
    }

    private void createForm() {
        FormPanel simple = new FormPanel();

        simple.setHeaderVisible(false);
        simple.setFrame(false);
        simple.setWidth(480);




        simple.add(protectionLevelOptions, formData);


        String url = "http://" + com.google.gwt.user.client.Window.Location.getHostName() + "?uuid=" + entity.getEntity();

        if (com.google.gwt.user.client.Window.Location.getHostName().equals("127.0.0.1")) {
            url = "http://127.0.0.1:8888/nimbits.html?gwt.codesvr=127.0.0.1:9997&uuid=" + entity.getEntity();
        }

        Html h = new Html("<p>This file can be viewed in a full window by anyone by setting" +
                " the protection level below and sharing this url:</p><br>" +
                " <A href =\"" + url + "\">" + url + "</a>");
        vp.add(h);


        vp.add(simple);
    }

    ToolBar mainToolBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setHeight("");

        final Button buttonUpdate = createUpdateButton();

        final Button buttonSave = new Button("Save");

        buttonSave.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveAll()));
        buttonSave.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                try {
                    saveFile();
                } catch (NimbitsException ignored) {

                }
            }
        });


        toolBar.add(buttonSave);

        toolBar.add(buttonUpdate);



        return toolBar;


    }



    private Button createUpdateButton() {
        final Button buttonUpdate = new Button("Update");
        buttonUpdate.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.album()));

        buttonUpdate.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
                w.setAutoWidth(true);
                w.setHeading("Upload a file");
                final FileUploadPanel p = new FileUploadPanel(UploadType.updatedFile, entity);
                p.addFileAddedListeners(new FileUploadPanel.FileAddedListener() {
                    @Override
                    public void onFileAdded() {

                        w.hide();
                        //   notifyDiagramClickedListener();
                        //  reloadTree();
                    }
                });

                w.add(p);
                w.show();
            }
        });
        return buttonUpdate;
    }

    private void saveFile() throws NimbitsException {

        final EntityServiceAsync serviceAsync = GWT.create(BlobService.class);
        entity.setProtectionLevel(protectionLevelOptions.getProtectionLevel());

        serviceAsync.addUpdateEntity(entity, new AsyncCallback<Entity>() {

            @Override
            public void onFailure(Throwable throwable) {
               GWT.log(throwable.getMessage(), throwable);
            }

            @Override
            public void onSuccess(Entity entity) {
                MessageBox.info("Diagram Settings", "Diagram Updated", null);

            }
        });


    }
}
