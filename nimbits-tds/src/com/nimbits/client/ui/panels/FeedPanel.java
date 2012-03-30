package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
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

    private String feedOwnersUUID;
    private final User user;

    public FeedPanel(final User user) {

        this.user = user;
        feedOwnersUUID = user.getUuid();
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
            service.getFeed(10, feedOwnersUUID, new AsyncCallback<List<FeedValue>>() {
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
        service.getFeed(FEED_COUNT, feedOwnersUUID, new AsyncCallback<List<FeedValue>>() {
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
        Button btn = new Button("Refresh");
        btn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh()));
        btn.setIconAlign(Style.IconAlign.LEFT);

        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                 feedOwnersUUID = user.getUuid();
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
               feedOwnersUUID =gxtModelSelectionChangedEvent.getSelectedItem().getUUID();
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
        status.setWidth(200);
        group.add(status);
        group.setBodyBorder(true);


        group.add(btn);
        bar.add(group);

        ContentPanel main = new ContentPanel();
        main.setBorders(false);
        main.setBodyBorder(false);
        main.setScrollMode(Style.Scroll.ALWAYS);
        main.setHeaderVisible(false);
        main.setTopComponent(bar);

        main.add(view);


        add(main);


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
//            set(Const.PARAM_VALUE,value.getUuid());
//            set(Const.Params.PARAM_NAME, value.getEmail());
//        }
//
//        public User getUser() {
//            return user;
//        }
//    }

}

