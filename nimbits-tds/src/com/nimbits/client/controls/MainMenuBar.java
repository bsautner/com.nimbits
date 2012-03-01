package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.menu.*;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.connection.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.panels.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.user.*;
import com.nimbits.shared.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/23/12
 * Time: 3:55 PM
 */
public class MainMenuBar extends ToolBar {
    private UserServiceAsync service;
    private int connectionCount = 0;
    private  LoginInfo loginInfo;
    public MainMenuBar(LoginInfo loginInfo, Map<String, String> settings) {
        this.loginInfo = loginInfo;
        service = GWT.create(UserService.class);
        addFileMenu();
        addNavigateMenu();
        addActionMenu();
        addOptionsMenu();

        if (loginInfo.isUserAdmin()) {
            addAdminMenu();
        }
        addHelpMenu();
        add(new SeparatorMenuItem());

        add(saveButton());
        add(addChartButton());

        add(connectionButton());
        add(pendingConnectionsButton());
        add(actionMenuItem("Logout",
                AbstractImagePrototype.create(Icons.INSTANCE.deleteFriend()),
                Action.logout));

    }

    private void addFileMenu() {
        Button fileButton = new Button("File");
        Menu fileMenu = new Menu();
        fileMenu.add(newDataPoint());
        fileMenu.add(newFolder());
        fileMenu.add(uploadFile());


        fileButton.setMenu(fileMenu);
        add(fileButton);
    }


    private void addNavigateMenu() {
        Button button = new Button("Navigate");
        Menu menu = new Menu();
        menu.add(actionMenuItem("Toggle Expansion",
                AbstractImagePrototype.create(Icons.INSTANCE.expand()),
                Action.expand));


        button.setMenu(menu);
        add(button);
    }
    private void addOptionsMenu() {
        Button button = new Button("Options");
        Menu menu = new Menu();


        CheckBox saveToNowCheckBox = new CheckBox();
        CheckBox autoSaveCheckBox = new CheckBox();

        menu.add(saveToNowCheckBox);
        saveToNowCheckBox.setBoxLabel("Save with Current Time");
        saveToNowCheckBox.setValue(true);
        autoSaveCheckBox.setBoxLabel("Auto-Save when a number is entered");
        autoSaveCheckBox.setValue(true);
        menu.add(autoSaveCheckBox);
        button.setMenu(menu);
        add(button);
    }
    private void addAdminMenu() {
        Button button = new Button("Admin");
        Menu menu = new Menu();
        menu.add(urlMenuItem("Run System Maintenance Service",
                AbstractImagePrototype.create(Icons.INSTANCE.expand()),
                "http://" + com.google.gwt.user.client.Window.Location.getHostName()+ "/cron/SystemMaint"));


        button.setMenu(menu);
        add(button);
    }
    private void addHelpMenu() {
        Button button = new Button("Help");
        Menu menu = new Menu();

        menu.add(urlMenuItem("Forum",
                AbstractImagePrototype.create(Icons.INSTANCE.Help()),
                "http://groups.google.com/group/nimbits"));
        menu.add(urlMenuItem("nimbits.com",
                AbstractImagePrototype.create(Icons.INSTANCE.Home()),
                "http://www.nimbits.com"));

        button.setMenu(menu);
        add(button);
    }
    private void addActionMenu() {
        Button button = new Button("Action");
        Menu menu = new Menu();

        menu.add(newKeyButton());

        //if (settings.containsKey(Const.SETTING_TWITTER_CLIENT_ID) && !Utils.isEmptyString(settings.get(Const.SETTING_TWITTER_CLIENT_ID)) && loginInfo != null)
        {
            menu.add(actionMenuItem("Enable Facebook",
                    AbstractImagePrototype.create(Icons.INSTANCE.connection()),
                    Action.facebook));
        }
        // if (settings.containsKey(Const.SETTING_FACEBOOK_API_KEY) && !Utils.isEmptyString(settings.get(Const.SETTING_FACEBOOK_API_KEY)))
        {
            menu.add(actionMenuItem("Enable Twitter",
                    AbstractImagePrototype.create(Icons.INSTANCE.connection()),
                    Action.twitter));
        }
        menu.add(actionMenuItem("Enable Instant Message (XMPP)",
                AbstractImagePrototype.create(Icons.INSTANCE.list_items()),
                Action.xmpp));

        button.setMenu(menu);
        add(button);
    }

    private MenuItem newDataPoint() {
        MenuItem item = new MenuItem("Data Point");

        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.addNew()));
        item.setToolTip(Const.MESSAGE_NEW_POINT);
        item.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                final MessageBox box = MessageBox.prompt(
                        Const.MESSAGE_NEW_POINT,
                        Const.MESSAGE_NEW_POINT_PROMPT);

                box.addCallback(createNewPointListener);
            }
        });

        return item;


    }

    private MenuItem uploadFile() {
        MenuItem item = new MenuItem("Upload File");

        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.diagram()));

        item.addListener(Events.OnClick, uploadFileListener);

        return item;


    }

    private final Listener<BaseEvent> uploadFileListener = new Listener<BaseEvent>() {


        @Override
        public void handleEvent(BaseEvent be) {
            final Window w = new Window();
            w.setAutoWidth(true);
            w.setHeading(Const.MESSAGE_UPLOAD_SVG);
            FileUploadPanel p = new FileUploadPanel(UploadType.newFile);
            p.addFileAddedListeners(new FileUploadPanel.FileAddedListener() {

                @Override
                public void onFileAdded()  {
                    w.hide();
                    notifyEntityModifiedListener(null, Action.refresh);

                }
            });

            w.add(p);
            w.show();
        }
    };


    private MenuItem actionMenuItem(final String text,
                                    final AbstractImagePrototype icon,
                                    final Action action) {
        MenuItem item = new MenuItem(text);

        item.setIcon(icon);

        item.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                notifyActionListener(action);
            }
        });

        return item;


    }

    private MenuItem urlMenuItem(final String text,
                                 final AbstractImagePrototype icon,
                                 final String url) {
        MenuItem item = new MenuItem(text);

        item.setIcon(icon);

        item.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                com.google.gwt.user.client.Window.open(url, "", "");
            }
        });

        return item;


    }



    private MenuItem newFolder() {
        MenuItem item = new MenuItem("New Folder");

        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.category()));

        item.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                final MessageBox box = MessageBox.prompt(
                        Const.MESSAGE_ADD_CATEGORY,
                        "Add a new folder to organize your data. Folders can be shared, and " +
                                "subscribed to by other users if you set their security level " +
                                "to public");

                box.addCallback(createNewFolderListener);
            }
        });

        return item;


    }
    private MenuItem newKeyButton() {
        MenuItem item = new MenuItem("Get or Reset Secret Key");
        item.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Key()));
        item.addListener(Events.OnClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                service.getSecret(new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log(throwable.getMessage(), throwable);
                    }

                    @Override
                    public void onSuccess(String s) {
                        MessageBox.confirm("Reset Your Key",
                                "Your secret Key is currently set to: " + s +
                                        "<br> Press YES to generate a new secret key and to have it emailed to the account you are currently logged in with. " +
                                        "Your old key will no longer be valid. You can use your key to use Nimbits web services.",
                                newKeyListener);

                    }
                });

            }
        });
        return item;
    }
    private final Listener<MessageBoxEvent> createNewFolderListener = new Listener<MessageBoxEvent>() {

        @Override
        public void handleEvent(final MessageBoxEvent be) {
            final String newEntityName = be.getValue();
            if (! Utils.isEmptyString(newEntityName))  {
                final EntityName categoryName = CommonFactoryLocator.getInstance().createName(newEntityName);

                final EntityServiceAsync service = GWT.create(EntityService.class);
                Entity entity = EntityModelFactory.createEntity(categoryName, EntityType.category);

                service.addUpdateEntity(entity,
                        new AsyncCallback<Entity>() {
                            @Override
                            public void onFailure(Throwable caught) {

                                Info.display(Const.WORD_ERROR,
                                        caught.getMessage());
                            }

                            @Override
                            public void onSuccess(final Entity result) {
                                notifyEntityModifiedListener(new GxtModel(result), Action.create);

                            }
                        });


            }
        }
    };

    private final Listener<MessageBoxEvent> createNewPointListener = new Listener<MessageBoxEvent>() {
        private String newEntityName;

        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                box.show();
                EntityServiceAsync service = GWT.create(EntityService.class);
                EntityName name = CommonFactoryLocator.getInstance().createName(newEntityName);
                //     Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
                service.addUpdateEntity(name, EntityType.point,  new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Info.display("Could not create "
                                + newEntityName,
                                caught.getMessage());
                        box.close();
                    }

                    @Override
                    public void onSuccess(Entity result) {

                        notifyEntityModifiedListener(new GxtModel(result), Action.create);

                        box.close();
                    }
                });


            }
        }
    };
    private final Listener<MessageBoxEvent> newKeyListener= new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent ce) {
                Button btn = ce.getButtonClicked();
                final UserServiceAsync us = GWT.create(UserService.class);

                if (btn.getText().toLowerCase().equals("yes")) {
                    us.updateSecret(new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {


                        }

                        @Override
                        public void onSuccess(String key) {
                            com.google.gwt.user.client.Window.alert("Your new secret has been reset to: " + key + " and a copy has been emailed to you. Your old secret key is no longer valid.");

                        }

                    });

                }

            }
        };


    private Button pendingConnectionsButton() {
        final Button connectionRequest = new Button("Connection Requests(" + connectionCount + ")");

        connectionRequest.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.add16()));
        service.getPendingConnectionRequests(loginInfo.getEmailAddress(), new AsyncCallback<List<Connection>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);

            }

            @Override
            public void onSuccess(final List<Connection> result) {


                if (result.size() > 0) {
                    final Menu scrollMenu = new Menu();
                    scrollMenu.setMaxHeight(200);
                    for (final Connection r : result) {
                        final MenuItem m = acceptConnectionMenuItem(scrollMenu, r);
                        scrollMenu.add(m);
                    }


                    connectionRequest.setMenu(scrollMenu);
                    connectionCount = result.size();

                    connectionRequest.setText("Requests(" + connectionCount + ")");
                }
                else {
                    connectionRequest.setVisible(false);
                }
            }

            private MenuItem acceptConnectionMenuItem(final Menu scrollMenu, final Connection r) {
                final MenuItem m = new MenuItem(r.getRequestorEmail().getValue());
                m.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.connection()));
                m.addListener(Events.Select, new Listener<BaseEvent>() {

                    @Override
                    public void handleEvent(final BaseEvent be) {
                        //	final Dialog simple = new Dialog();
                        //simple.setHeading(");
                        final MessageBox box = new MessageBox();
                        box.setButtons(MessageBox.YESNOCANCEL);
                        box.setIcon(MessageBox.QUESTION);
                        box.setTitle("Connection request approval");
                        box.addCallback(new Listener<MessageBoxEvent>() {
                            @Override
                            public void handleEvent(final MessageBoxEvent be) {

                                final Button btn = be.getButtonClicked();

                                if (btn.getText().equals("Yes")) {
                                    acceptConnection(r, true);
                                    scrollMenu.remove(m);
                                } else if (btn.getText().equals("No")) {
                                    scrollMenu.remove(m);
                                    acceptConnection(r, false);
                                }
                            }

                            private void acceptConnection(
                                    final Connection r,
                                    boolean accepted)  {
                                UserServiceAsync userService;
                                userService = GWT.create(UserService.class);
                                userService.connectionRequestReply(r.getTargetEmail(), r.getRequestorEmail(), r.getUUID(), accepted, new AsyncCallback<Void>() {

                                    @Override
                                    public void onFailure(Throwable e) {
                                        GWT.log(e.getMessage(), e);

                                    }

                                    @Override
                                    public void onSuccess(Void result) {

                                        connectionCount += (-1);

                                        connectionRequest.setText("Requests(" + connectionCount + ")");
                                        notifyEntityModifiedListener(null, Action.refresh);

                                    }

                                });
                            }


                        });

                        box.setMessage("The owner of the email address: '" + r.getRequestorEmail().getValue() + "' would like to connect with you. You will have read only access to each others data points. Is that OK?");
                        box.show();


                    }

                });
                return m;
            }


        });
        return connectionRequest;
    }
    public interface EntityModifiedListener {
        void onEntityModified(final GxtModel model, final Action action) ;

    }
    private List<EntityModifiedListener> entityModifiedListeners = new ArrayList<EntityModifiedListener>();
    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    void notifyEntityModifiedListener(final GxtModel model, final Action action)  {
        for (EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }


    private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
    public interface ActionListener {
        void onAction(Action action) ;

    }

    public void addActionListeners(final ActionListener listener) {
        this.actionListeners.add(listener);
    }

    void notifyActionListener(Action action)  {
        for (ActionListener listener : actionListeners) {
            listener.onAction(action);
        }
    }

    private Button connectionButton() {
        final Button b = new Button("Send Connection Request");
        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.addFriend()));

        b.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {


                final MessageBox box = MessageBox.prompt("Connect to other Nimbits Users",
                        "Enter an email address to invite a friend to connect their Data Points to yours. After they approve your request " +
                                "you'll be able to see each others data points and diagrams (based on permission levels).");
                box.addCallback(sendInviteLisenter());

            }
        });
        return b;
    }

    private Listener<MessageBoxEvent> sendInviteLisenter() {
        return new Listener<MessageBoxEvent>() {
            @Override
            public void handleEvent(MessageBoxEvent be) {
                final String email;
                email = be.getValue();
                if (email != null) {
                    if (email.length() > 0) {
                        UserServiceAsync userService;
                        userService = GWT.create(UserService.class);
                        EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
                        userService.sendConnectionRequest(emailAddress, new AsyncCallback<Void>() {

                            @Override
                            public void onFailure(Throwable caught) {


                            }

                            @Override
                            public void onSuccess(Void result) {

                                Info.display("Connection Request", "Connection Request Sent!");

                            }

                        });


                    }
                }
            }

        };
    }
    private Button saveButton() {
        Button saveButton = new Button("Save");
        saveButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveAll()));
        saveButton.setToolTip("Save checked rows");

        saveButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent baseEvent) {
                notifyActionListener(Action.save);
            }
        });
        return (saveButton);
    }

    private Button addChartButton() {
        Button addChartButton = new Button("Add Chart");
        addChartButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.chart24()));
        addChartButton.setToolTip("Add another chart");

        addChartButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent baseEvent) {
                notifyActionListener(Action.addChart);

            }
        });
        return addChartButton;

    }
}
