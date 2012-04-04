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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FeedType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.GxtFeedModel;
import com.nimbits.client.model.GxtModel;
import com.nimbits.client.model.feed.FeedValue;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.feed.Feed;
import com.nimbits.client.service.feed.FeedAsync;
import com.nimbits.client.ui.controls.EntityCombo;
import com.nimbits.client.ui.icons.Icons;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:27 PM
 */
public class FeedPanel  extends LayoutContainer {
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
        Timer updater = new Timer() {
            @Override
            public void run() {

                updateValues(false);

            }
        };
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
        super.onAttach();
    }

    private ComboBox<FeedTypeOption> optionComboBox(final FeedType selectedValue) {
        ComboBox<FeedTypeOption> combo = new ComboBox<FeedTypeOption>();

        ArrayList<FeedTypeOption> ops = new ArrayList<FeedTypeOption>();

        for (FeedType type : FeedType.values()) {
            ops.add(new FeedTypeOption(type));
        }


        ListStore<FeedTypeOption> store = new ListStore<FeedTypeOption>();

        store.add(ops);

        //  combo.setFieldLabel(title);
        combo.setDisplayField(Parameters.name.getText());
        combo.setValueField(Parameters.value.getText());
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        FeedTypeOption selected = combo.getStore().findModel(Parameters.value.getText(), selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void updateValues(boolean reset) {
        final ListStore<GxtFeedModel> store  = view.getStore();
        if (reset) {
            store.removeAll();
        }
        if (store != null) {
            FeedAsync service = GWT.create(Feed.class);
            service.getFeed(10, connectionEntityKey, new AsyncCallback<List<FeedValue>>() {
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
            });
        }
    }



    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setBorders(false);
       // setScrollMode(Style.Scroll.AUTOY);

        ContentPanel panel = new ContentPanel();
        panel.setLayout(new RowLayout(Style.Orientation.HORIZONTAL));
        panel.setHeight(1200);
        panel.setWidth("100%");
        panel.setHeaderVisible(false);
        view = new ListView<GxtFeedModel>() {
            @Override
            protected GxtFeedModel prepareData(GxtFeedModel model) {
                // String s = model.get(Const.Params.PARAM_NAME);
                //  model.set("shortName", Format.ellipse(s, 15));
                model.set(Parameters.path.getText(), GWT.getHostPageBaseURL() + model.get(Parameters.path.getText()));
                return model;
            }

        };


        final FeedAsync service = GWT.create(Feed.class);
        final int FEED_COUNT = 30;
        service.getFeed(FEED_COUNT, connectionEntityKey, new AsyncCallback<List<FeedValue>>() {
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
        });

        view.setTemplate(getTemplate());
        view.setBorders(false);
        view.setItemSelector("div.thumb-wrap");
        view.setStyleAttribute("overflow-y", "scroll");
        view.getSelectionModel().addListener(Events.SelectionChange,
                new Listener<SelectionChangedEvent<BeanModel>>() {

                    public void handleEvent(SelectionChangedEvent<BeanModel> be) {
//                        panel.setHeading("Simple ListView (" + be.getSelection().size()
//                                + " items selected)");
                    }

                });

        ToolBar bar = new ToolBar();

        ButtonGroup group = new ButtonGroup(1);
        group.setHeading("Feed Options");
        group.setHeaderVisible(false);
        group.setBodyBorder(false);
        group.setAutoWidth(true);
        group.setWidth("100%");
        group.setBorders(false);
        Button btn = new Button("Refresh");
        btn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh()));
        btn.setIconAlign(Style.IconAlign.LEFT);

        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                 connectionEntityKey = user.getKey();
            }
        });


        feedType = optionComboBox(FeedType.all);
        feedType.addSelectionChangedListener(new SelectionChangedListener<FeedTypeOption>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<FeedTypeOption> feedTypeOptionSelectionChangedEvent) {

                updateValues(true);
            }
        });
        group.add(new LabelToolItem("Show:"));
        group.add(feedType);
        group.setBodyBorder(true);



        group.add(new LabelToolItem("Switch to connected user's Feed:"));
        EntityCombo entityCombo = new EntityCombo(EntityType.userConnection, "", "");
        entityCombo.addSelectionChangedListener(new SelectionChangedListener<GxtModel>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<GxtModel> gxtModelSelectionChangedEvent) {
               connectionEntityKey =gxtModelSelectionChangedEvent.getSelectedItem().getUUID();
                updateValues(true);
            }
        });
        group.add(entityCombo);

        group.add(new LabelToolItem("Update Status:"));
        final TextArea status = new TextArea();
        status.addKeyListener(new KeyListener() {
            @Override
            public void componentKeyDown(ComponentEvent event) {
                if (event.getKeyCode() == 13) {
                    service.postToFeed(user,status.getValue(), FeedType.status, new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            reload();
                        }

                        @Override
                        public void onSuccess(Void aVoid) {
                           updateValues(false);
                           status.setValue("");
                        }
                    });
                }
            }
        });
        status.setWidth("100%");
        group.add(status);
        group.setBodyBorder(true);


        group.add(btn);
        bar.add(group);
        panel.setTopComponent(bar);
        panel.add(view, new RowData(1, -1, new Margins(4)));
        view.setBorders(true);
        view.setHeight(600);
        panel.setFrame(true);


        add(panel, new FlowData(-1));


    }

    private native String getTemplate() /*-{
        return ['<tpl for=".">',
            '<table border = 0>',
            '<tr><td>{html}</td></tr>',
            '</table>',
            '<hr />',
            '</tpl>',
            '<div class="x-clear"></div>'].join("");

    }-*/;

    public void reload() {
        updateValues(true);

    }

    private class FeedTypeOption extends BaseModelData {
        FeedType type;


        public FeedTypeOption(FeedType value) {
            this.type = value;
            set(Parameters.value.getText(), value.getCode());
            set(Parameters.name.getText(), value.getText());
        }

        public FeedType getMethod() {
            return type;
        }
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

}

