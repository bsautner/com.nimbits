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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.controls.ProtectionLevelOptions;
import com.nimbits.client.enums.UploadType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.service.blob.BlobService;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/31/11
 * Time: 1:05 PM
 */
class DiagramPropertyPanel extends NavigationEventProvider {

    //private final Diagram diagram;
    private final Entity entity;


    private final ProtectionLevelOptions protectionLevelOptions;

//      public interface DiagramDeletedListener {
//        public void onDiagramDeleted(Diagram p);
//
//    }
//     private List<DiagramDeletedListener> diagramDeletedListeners = new ArrayList<DiagramDeletedListener>();
//    public void addDiagramDeletedListeners(DiagramDeletedListener listener) {
//        diagramDeletedListeners.add(listener);
//    }
//
//    private void notifyDiagramDeletedListener(Diagram p) {
//        for (DiagramDeletedListener DiagramDeletedListener : diagramDeletedListeners) {
//            DiagramDeletedListener.onDiagramDeleted(p);
//        }
//    }


    DiagramPropertyPanel(final Entity entity) {
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


        String url = "http://" + com.google.gwt.user.client.Window.Location.getHostName() + "?uuid=" + entity.getUUID();

        if (com.google.gwt.user.client.Window.Location.getHostName().equals("127.0.0.1")) {
            url = "http://127.0.0.1:8888/nimbits.html?gwt.codesvr=127.0.0.1:9997&uuid=" + entity.getUUID();
        }

        Html h = new Html("<p>This diagram can be viewed in a full window by anyone by setting" +
                " the protection level below and sharing this url:</p><br>" +
                " <A href =\"" + url + "\">" + url + "</a>");
        vp.add(h);


        vp.add(simple);
    }

    ToolBar mainToolBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setHeight("");


        final Button buttonDelete = createDeleteButton();
        final Button buttonUpdate = createUpdateButton();

        final Button buttonSave = new Button("Save");

        buttonSave.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveAll()));
        buttonSave.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                try {
                    saveDiagram();
                } catch (NimbitsException ignored) {

                }
            }
        });


        toolBar.add(buttonSave);

        toolBar.add(buttonUpdate);

        toolBar.add(buttonDelete);
        // buttonDelete.setWidth("90px");


        return toolBar;


    }

    private Button createDeleteButton() {
        final Button buttonDelete = new Button("Delete");

        buttonDelete.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.delete()));
        final EntityServiceAsync service = GWT.create(EntityService.class);

        final Listener<MessageBoxEvent> deleteDiagramListener = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                Button btn = ce.getButtonClicked();

                if (btn.getText().equals("Yes")) {
                    service.deleteEntity(entity, new AsyncCallback<Void>() {

                        @Override
                        public void onFailure(Throwable caught) {


                        }

                        @Override
                        public void onSuccess(Void result) {
                          notifyEntityDeletedListener(entity);
                        }

                    });

                }

            }
        };
        buttonDelete.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                MessageBox.confirm("Confirm", "Are you sure you want delete this diagram?", deleteDiagramListener);
            }
        });
        return buttonDelete;
    }

    private Button createUpdateButton() {
        final Button buttonUpdate = new Button("Update");
        buttonUpdate.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.album()));

        buttonUpdate.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
                w.setAutoWidth(true);
                w.setHeading("Upload a process diagram in .svg format");
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

    private void saveDiagram() throws NimbitsException {

        final EntityServiceAsync serviceAsync = GWT.create(BlobService.class);
        entity.setProtectionLevel(protectionLevelOptions.getProtectionLevel());

        serviceAsync.addUpdateEntity(entity, new AsyncCallback<Entity>() {

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Entity entity) {
                MessageBox.info("Diagram Settings", "Diagram Updated", null);

            }
        });


    }
}
