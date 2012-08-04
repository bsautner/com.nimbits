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
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.ui.icons.Icons;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/31/11
 * Time: 1:05 PM
 */
public class CategoryPropertyPanel extends NavigationEventProvider {

    private final Entity entity;


    private final RadioGroup radioGroup = new RadioGroup();
    private final Radio radioProtection0 = new Radio();
    private final Radio radioProtection1 = new Radio();
    private final Radio radioProtection2 = new Radio();
    private final TextArea description = new TextArea();


    public CategoryPropertyPanel(final Entity entity) {
        this.entity = entity;

    }

    private VerticalPanel vp;

    private FormData formData;

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        ContentPanel p = new ContentPanel();
        p.setHeaderVisible(false);
        p.setTopComponent(mainToolBar());
        p.setFrame(false);
        p.setBodyBorder(false);
        p.setLayout(new FillLayout());
        //p.setHeight(500);

        formData = new FormData("-20");
        vp = new VerticalPanel();
        vp.setLayout(new FillLayout());

        // vp.setSpacing(10);
        createForm();

        String url = "http://" + com.google.gwt.user.client.Window.Location.getHostName() + "?" + Parameters.uuid.getText() + "=" + entity.getKey();


        Html h = new Html("<p>Link:</p><br>" +
                " <A href =\"" + url + "\">" + url + "</a>");
        vp.add(h);
        p.add(vp);

        add(p);
    }

    private void createForm() {
        FormPanel simple = new FormPanel();

        simple.setHeaderVisible(false);
        simple.setFrame(false);
        simple.setBodyBorder(false);
        simple.setLayout(new FillLayout());
        //  simple.setWidth(480);


        radioProtection0.setBoxLabel("Only Me");
        radioProtection0.setValue(entity.getProtectionLevel().equals(ProtectionLevel.onlyMe));


        radioProtection1.setBoxLabel("My Connections");
        radioProtection1.setValue(entity.getProtectionLevel().equals(ProtectionLevel.onlyConnection));


        radioProtection2.setBoxLabel("Anyone");
        radioProtection2.setValue(entity.getProtectionLevel().equals(ProtectionLevel.everyone));


        radioGroup.setFieldLabel("Who can view");

        radioGroup.add(radioProtection0);
        radioGroup.add(radioProtection1);
        radioGroup.add(radioProtection2);
        simple.add(radioGroup, formData);


        description.setPreventScrollbars(true);
        description.setValue(entity.getDescription());
        description.setFieldLabel("Description");
        simple.add(description, new FormData("-20"));
        description.setSize("400", "100");

        simple.add(description);

        vp.add(simple);
    }

    ToolBar mainToolBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setHeight("");


        final Button buttonSave = new Button("Save");

        buttonSave.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveAll()));
        buttonSave.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

                    save();

            }
        });


        toolBar.add(buttonSave);


        return toolBar;


    }

    private void save() {

        final EntityServiceAsync service = GWT.create(EntityService.class);
        if (radioProtection0.getValue()) {
            entity.setProtectionLevel(ProtectionLevel.onlyMe);
        } else if (radioProtection1.getValue()) {
            entity.setProtectionLevel(ProtectionLevel.onlyConnection);
        } else if (radioProtection2.getValue()) {
            entity.setProtectionLevel(ProtectionLevel.everyone);
        }

        entity.setDescription(description.getValue());

        service.addUpdateEntity(entity, new AsyncCallback<Entity>() {

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Entity entity1) {

            }
        });


    }
}
