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

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.TreeModel;
import com.nimbits.client.model.feed.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.feed.*;
import com.nimbits.client.ui.controls.*;
import com.nimbits.client.ui.icons.*;

import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:27 PM
 */
public class FeedPanel  extends LayoutContainer {
    private static final int HEIGHT = 1200;
    private static final int COUNT = 30;
    private static final int HEIGHT1 = 600;
    private static final int WIDTH = 150;
    ListView<GxtFeedModel> view;
    //ContentPanel panel;
    private ComboBox<FeedTypeOption> feedType;

    private String connectionEntityKey;
    private final User user;

    public FeedPanel(final User user) {

        this.user = user;
        connectionEntityKey = user.getKey();
    }

    @Override
    protected void onAttach() {
        final Timer updater = new RefreshTimer();
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
        super.onAttach();
    }

    private static ComboBox<FeedTypeOption> optionComboBox(final FeedType selectedValue) {
        final ComboBox<FeedTypeOption> combo = new ComboBox<FeedTypeOption>();

        final List<FeedTypeOption> ops = new ArrayList<FeedTypeOption>(FeedType.values().length);

        for (final FeedType type : FeedType.values()) {
            ops.add(new FeedTypeOption(type));
        }


        final ListStore<FeedTypeOption> store = new ListStore<FeedTypeOption>();

        store.add(ops);

        //  combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        final FeedTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void updateValues(final boolean reset) {
        final ListStore<GxtFeedModel> store  = view.getStore();
        if (reset) {
            store.removeAll();
        }
        if (store != null) {
            final FeedAsync service = GWT.create(Feed.class);
            service.getFeed(10, connectionEntityKey, new UpdateValuesAsyncCallback(store));
        }
    }



    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        setBorders(false);
       // setScrollMode(Style.Scroll.AUTOY);

        final ContentPanel panel = new ContentPanel();
        panel.setLayout(new RowLayout(Style.Orientation.HORIZONTAL));
        panel.setHeight(HEIGHT);
        panel.setWidth("100%");
        panel.setHeaderVisible(false);
        view = new GxtFeedModelListView();


        final FeedAsync service = GWT.create(Feed.class);

        service.getFeed(COUNT, connectionEntityKey, new ListAsyncCallback());

        view.setTemplate(getTemplate());
        view.setBorders(false);
        view.setItemSelector("div.thumb-wrap");
        view.setStyleAttribute("overflow-y", "scroll");
        view.setStyleAttribute("white-space", "normal");
        view.setWidth(WIDTH);
        view.getSelectionModel().addListener(Events.SelectionChange,
                new SelectionChangedEventListener());

        final ToolBar bar = feedToolbar();
        panel.setTopComponent(bar);
        panel.add(view, new RowData(1, -1, new Margins(0)));
        view.setBorders(true);
        view.setHeight(HEIGHT1);
        panel.setFrame(true);


        add(panel, new FlowData(-1));


    }

    private ToolBar feedToolbar() {
        final FeedAsync service = GWT.create(Feed.class);
        final Button btn = new Button("Refresh");

        final ToolBar bar = new ToolBar();

        final ButtonGroup group = new ButtonGroup(1);
        group.setHeading("Feed Options");
        group.setHeaderVisible(false);
        group.setBodyBorder(false);
        group.setAutoWidth(true);
        group.setWidth("100%");
        group.setBorders(false);
        group.setFrame(false);

        btn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh()));
        btn.setIconAlign(Style.IconAlign.LEFT);
        btn.addSelectionListener(new RefreshButtonEventSelectionListener());


        feedType = optionComboBox(FeedType.all);
        feedType.addSelectionChangedListener(new FeedTypeOptionSelectionChangedListener());

        final EntityCombo entityCombo = new EntityCombo(EntityType.userConnection, "", "");
        entityCombo.addSelectionChangedListener(new ConnectionSelectionChangedListener());


        final TextArea status = new TextArea();
        status.addKeyListener(new StatusKeyDownListener(service, status));
        status.setWidth("100%");

        group.add(new LabelToolItem("Show:"));
        group.add(feedType);
        group.add(new LabelToolItem("Switch to connected user's Feed:"));

        group.add(entityCombo);
        group.add(new LabelToolItem("Update Status:"));
        group.add(status);


        group.add(btn);
        bar.add(group);
        return bar;
    }

    private native String getTemplate() /*-{
        return ['<tpl for=".">',
            '<table border=0 style="width:150px;" >',
            '<tr><td><div style="width:150px;float:left">{html}</div></td></tr>',
            '</table>',
            '<hr />',
            '</tpl>',
            '<div class="x-clear"></div>'].join("");

    }-*/;


    public void reload() {
        updateValues(true);

    }

    private static class GxtFeedModelListView extends ListView<GxtFeedModel> {
        @Override
        protected GxtFeedModel prepareData(GxtFeedModel model) {
            // String s = model.get(Const.Params.PARAM_NAME);
            //  model.set("shortName", Format.ellipse(s, 15));
            model.set(Parameters.path.getText(), GWT.getHostPageBaseURL() + model.get(Parameters.path.getText()));
            return model;
        }

    }

    private static class SelectionChangedEventListener implements Listener<SelectionChangedEvent<BeanModel>> {

        @Override
        public void handleEvent(SelectionChangedEvent<BeanModel> be) {
//                        panel.setHeading("Simple ListView (" + be.getSelection().size()
//                                + " items selected)");
        }

    }

    private static class FeedTypeOption extends BaseModelData {
        FeedType type;


        private FeedTypeOption(FeedType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

//        public FeedType getMethod() {
//            return type;
//        }
    }


//    private class UserOption extends BaseModelData {
//        User user;
//
//
//        public UserOption(User value) {
//            this.user = value;
//            set(Const.PARAM_VALUE,value.getKey());
//            set(Const.Params.PARAM_NAME, value.getEmail());
//        }
//
//        public User getUser() {
//            return user;
//        }
//    }

    private class ListAsyncCallback implements AsyncCallback<List<FeedValue>> {
        @Override
        public void onFailure(Throwable caught) {

        }

        @Override
        public void onSuccess(List<FeedValue> result) {
            final ListStore<GxtFeedModel> store = new ListStore<GxtFeedModel>();

            for (final FeedValue v : result) {
                store.add(new GxtFeedModel(v));
            }
            view.setStore(store);

        }
    }

    private class RefreshTimer extends Timer {
        @Override
        public void run() {

            updateValues(false);

        }
    }

    private class UpdateValuesAsyncCallback implements AsyncCallback<List<FeedValue>> {
        private final ListStore<GxtFeedModel> store;

        private UpdateValuesAsyncCallback(ListStore<GxtFeedModel> store) {
            this.store = store;
        }

        @Override
        public void onFailure(Throwable caught) {

        }

        @Override
        public void onSuccess(final List<FeedValue> result) {

            GxtFeedModel model;
            for (final FeedValue v : result) {

                model = new GxtFeedModel(v);
                FeedType type = feedType.getValue().type;
                if (type.equals(FeedType.all) || type.equals(v.getFeedType())) {
                    if (store.findModel(Parameters.html.getText(), model.getHtml()) == null) {
                        store.insert(model, 0);
                   }
                }
            }

            layout(true);
        }
    }

    private class RefreshButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
             connectionEntityKey = user.getKey();
        }
    }

    private class FeedTypeOptionSelectionChangedListener extends SelectionChangedListener<FeedTypeOption> {
        @Override
        public void selectionChanged(SelectionChangedEvent<FeedTypeOption> feedTypeOptionSelectionChangedEvent) {

            updateValues(true);
        }
    }

    private class ConnectionSelectionChangedListener extends SelectionChangedListener<TreeModel> {
        @Override
        public void selectionChanged(SelectionChangedEvent<TreeModel> gxtModelSelectionChangedEvent) {
           connectionEntityKey =gxtModelSelectionChangedEvent.getSelectedItem().getUUID();
            updateValues(true);
        }
    }

    private class StatusKeyDownListener extends KeyListener {
        private static final int ENTER_KEY = 13;
        private final FeedAsync service;
        private final TextArea status;

        private StatusKeyDownListener(FeedAsync service, TextArea status) {
            this.service = service;
            this.status = status;
        }

        @Override
        public void componentKeyDown(ComponentEvent event) {
            if (event.getKeyCode() == ENTER_KEY) {
                service.postToFeed(user, status.getValue(), FeedType.status, new PostToFeedAsyncCallback());
            }
        }

        private class PostToFeedAsyncCallback implements AsyncCallback<Void> {
            @Override
            public void onFailure(Throwable throwable) {
                reload();
            }

            @Override
            public void onSuccess(Void aVoid) {
               updateValues(false);
               status.setValue("");
            }
        }
    }
}

