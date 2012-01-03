package com.nimbits.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.nimbits.android.R;
import com.nimbits.android.account.OwnerAccountFactory;
import com.nimbits.android.dao.LocalDatabaseDaoFactory;
import com.nimbits.android.database.DatabaseHelperFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import org.apache.http.cookie.Cookie;

import java.util.List;


public class StartActivity extends Activity {

    private static final int LOAD_DIALOG = 0;
    private static final int POINT_DIALOG = 1;
    private static final int CHANGE_SERVER_DIALOG = 2;
    private static final int CHOOSE_SERVER_DIALOG = 3;
    private static final int CHECK_SERVER_DIALOG = 4;


    private AuthenticateThread authenticateThread;

    private ProgressDialog authenticateDialog;

    private String baseURL;
    private Cookie authCookie;

    private Cursor listCursor;

    private static String selectedServer = "";

    private WebView mWebView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_view);
        if (DatabaseHelperFactory.getInstance(StartActivity.this).checkDatabase()) {
            setContentView(R.layout.catagorylayout);
            showDialog(CHECK_SERVER_DIALOG);
        }
        //  getGPSLocation();
    }

    protected Dialog onCreateDialog(final int id) {
        switch (id) {


            case CHOOSE_SERVER_DIALOG:
                return dialogChooseServer();
            case CHECK_SERVER_DIALOG:
                return dialogAuthenticatedResponse();
            //  case NO_DATA_DIALOG:
            //     return dialogNoPoints();
            default:
                return null;
        }


    }


    private Dialog dialogChooseServer() {


        final List<String> servers = LocalDatabaseDaoFactory.getInstance().getServers(StartActivity.this);

        final CharSequence[] items = servers.toArray(new CharSequence[servers.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Nimbits Server");


        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int item) {


                selectedServer = (String) items[item];

                //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                // selection =  (String) items[item];

            }
        });

        builder.setNeutralButton("New", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dismissDialog(CHOOSE_SERVER_DIALOG);
                removeDialog(CHOOSE_SERVER_DIALOG);
                showDialog(CHANGE_SERVER_DIALOG);

            }

        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (selectedServer != null) {
                    SQLiteDatabase db1;
                    db1 = DatabaseHelperFactory.getInstance(StartActivity.this).getDB(true);
                    Log.v("delete", selectedServer);
                    db1.execSQL("delete from Servers where url='" + selectedServer + "'");
                    db1.close();
                    dismissDialog(CHOOSE_SERVER_DIALOG);
                    removeDialog(CHOOSE_SERVER_DIALOG);
                    showDialog(CHOOSE_SERVER_DIALOG);

                }

            }

        });


        builder.setPositiveButton("Switch", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (selectedServer != null) {
                    baseURL = selectedServer;
                    LocalDatabaseDaoFactory.getInstance().updateSetting(StartActivity.this, Const.PARAM_SERVER, baseURL);
                    dismissDialog(CHOOSE_SERVER_DIALOG);
                    removeDialog(CHOOSE_SERVER_DIALOG);

                    showDialog(CHECK_SERVER_DIALOG);

                }

            }
        }
        );


        return builder.create();

    }

    private Dialog dialogAddServer() {
        final Dialog dialog1 = new Dialog(StartActivity.this);

        dialog1.setContentView(R.layout.text_prompt);
        dialog1.setTitle("Change Server");
        TextView text = (TextView) dialog1.findViewById(R.id.text);
        text.setText("You can point your android device to another Nimbits Server URL (i.e yourserver.appspot.com)");

        EditText urlText = (EditText) dialog1.findViewById(R.id.new_value);
        urlText.setText(baseURL);
        Button b = (Button) dialog1.findViewById(R.id.textPromptOKButton);
        Button d = (Button) dialog1.findViewById(R.id.textPromptDefaultButton);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditText urlText = (EditText) dialog1.findViewById(R.id.new_value);
                // String authToken = OwnerAccountImpl.getToken(StartActivity.this);
                String u = urlText.getText().toString();
                LocalDatabaseDaoFactory.getInstance().addServer(StartActivity.this, u);
                dialog1.dismiss();
                removeDialog(CHANGE_SERVER_DIALOG);
                showDialog(CHOOSE_SERVER_DIALOG);


            }
        });

        d.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditText urlText = (EditText) dialog1.findViewById(R.id.new_value);
                urlText.setText(Const.PATH_NIMBITS_PUBLIC_SERVER);

            }
        });

        dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {

            public void onDismiss(DialogInterface dialog) {
            }

        });

        return dialog1;
    }

    private Dialog dialogAuthenticatedResponse() {
        Log.v("NimbitsV", "Authenticating");
        baseURL = LocalDatabaseDaoFactory.getInstance().getSetting(StartActivity.this, Const.PARAM_SERVER);
        Log.v("NimbitsV", "Logging into " + baseURL);
        authenticateDialog = new ProgressDialog(StartActivity.this);
        authenticateDialog = ProgressDialog.show(StartActivity.this, "", "Authenticating to Nimbits Server @ " + baseURL + " using account " + OwnerAccountFactory.getInstance().getEmail(StartActivity.this) + ".  Please wait...", true);

        authenticateThread = new AuthenticateThread(authenticateThreadHandler, StartActivity.this);
        authenticateThread.start();
        return authenticateDialog;
    }


    // Define the Handler that receives messages from the thread and update the progress
    private final Handler authenticateThreadHandler = new Handler() {
        public void handleMessage(Message msg) {
            //	int total = msg.getData().getInt("total");
            final boolean isLoggedIn = msg.getData().getBoolean(Const.PARAM_IS_LOGGED_IN);
            Log.v("NimbitsV", "is logged in " + isLoggedIn);
            if (authenticateDialog != null) {
                dismissDialog(CHECK_SERVER_DIALOG);
                removeDialog(CHECK_SERVER_DIALOG);
                authenticateThread.setState(AuthenticateThread.STATE_DONE);
                authenticateDialog = null;

                if (isLoggedIn) {

                    setContentView(R.layout.diagram_view);

                    CookieSyncManager.createInstance(StartActivity.this);
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeSessionCookie();


                    CookieSyncManager.getInstance().sync();
                    try {

                        authCookie = OwnerAccountFactory.getInstance().getNimbitsClient(StartActivity.this, baseURL).getAuthCookie();
                        if (authCookie != null) {
                            cookieManager.setCookie(baseURL, authCookie.getName() + "=" + authCookie.getValue() + "; domain=" + authCookie.getDomain());
                        }
                    } catch (NimbitsException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }


                    // if (json != null) {
                    // final Diagram diagram = GsonFactory.getInstance().fromJson(json, DiagramModel.class);
                    mWebView = (WebView) findViewById(R.id.webview);
                    mWebView.requestFocus(View.FOCUS_DOWN);
                    mWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                              if (url.contains("?" + Const.PARAM_CLIENT + "=" + Const.WORD_ANDROID)) {
//                                //  processPointClickedResponse(url, baseURL);
//                                return true;
//
//                            } else {
                            view.loadUrl(url);
                            return true;
//                            }

                        }
                    });
                    mWebView.getSettings().setSupportZoom(true);

                    mWebView.getSettings().setBuiltInZoomControls(true);
                    mWebView.setInitialScale(100);
                    mWebView.getSettings().setJavaScriptEnabled(true);
//            Map<String, String> headers = new HashMap<String, String>();
//            headers.put("Cookie", cookie);
                    mWebView.loadUrl(baseURL + "?" + Const.PARAM_CLIENT + "=" + Const.WORD_ANDROID);
                } else {
                    Toast.makeText(StartActivity.this, "Nimbits uses google accounts to authenticate. Please add a google.com (gmail.com) account to this device.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    @Override
    public void finish() {
        super.finish();


    }


    private void changeServer() {


        showDialog(CHOOSE_SERVER_DIALOG);
    }

    //Event Overrides

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.main:
                showDialog(CHECK_SERVER_DIALOG);
                return true;
            case R.id.exit:

                this.finish();
                return true;
            case R.id.Servers:
                changeServer();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


    private class AuthenticateThread extends Thread {
        final Handler m;
        final static int STATE_DONE = 0;
        int mState;
        final Context currentContext;

        AuthenticateThread(Handler h, Context c) {
            m = h;
            currentContext = c;
        }

        /* sets the current state for the thread,
  * used to stop the thread */
        public void setState(int state) {
            mState = state;
        }

        private void update(boolean isLoggedIn) {
            Message msg = m.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean(Const.PARAM_IS_LOGGED_IN, isLoggedIn);
            msg.setData(b);
            m.sendMessage(msg);
        }

        public void run() {

            try {
                baseURL = LocalDatabaseDaoFactory.getInstance().getSetting(StartActivity.this, Const.PARAM_SERVER);

                // String authToken = OwnerAccountImpl.getToken(currentContext);
                //googleAuth.connectClean(baseURL,authToken);
                boolean isLoggedIn = OwnerAccountFactory.getInstance().getNimbitsClient(StartActivity.this, baseURL).isLoggedIn();
                authCookie = OwnerAccountFactory.getInstance().getNimbitsClient(StartActivity.this, baseURL).getAuthCookie();
                if (!isLoggedIn) {
                    OwnerAccountFactory.getInstance().invalidateToken(StartActivity.this, OwnerAccountFactory.getInstance().getToken(StartActivity.this));
                    isLoggedIn = OwnerAccountFactory.getInstance().getNimbitsClient(StartActivity.this, baseURL).isLoggedIn();
                    authCookie = OwnerAccountFactory.getInstance().getNimbitsClient(StartActivity.this, baseURL).getAuthCookie();
                }
                update(isLoggedIn);
            } catch (Exception e) {
                OwnerAccountFactory.getInstance().invalidateToken(StartActivity.this, OwnerAccountFactory.getInstance().getToken(StartActivity.this));
                Log.e(Const.N, e.getMessage());
                update(false);
            }


        }

    }


}