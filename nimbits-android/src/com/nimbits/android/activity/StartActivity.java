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

package com.nimbits.android.activity;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
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
import com.nimbits.android.WebViewDialog;
import com.nimbits.android.account.OwnerAccountFactory;
import com.nimbits.android.dao.LocalDatabaseDaoFactory;
import com.nimbits.android.database.DatabaseHelperFactory;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import org.apache.http.cookie.Cookie;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class StartActivity extends Activity {

    private static final int LOAD_DIALOG = 0;
    private static final int CHANGE_SERVER_DIALOG = 2;
    private static final int CHOOSE_SERVER_DIALOG = 3;
    private static final int CHECK_SERVER_DIALOG = 4;
    private AuthenticateThread authenticateThread;
    private ProgressDialog authenticateDialog;
    private String baseURL;
    private Cookie authCookie;
    public String webViewURl;

    private static String selectedServer = "";
    public String webViewcookie;
    private WebView mWebView;
    Context currentContext;
    CookieManager cookieManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        CookieSyncManager.getInstance().startSync();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_view);

            if (DatabaseHelperFactory.getInstance(StartActivity.this).checkDatabase()) {
                setContentView(R.layout.catagorylayout);
                showDialog(CHECK_SERVER_DIALOG);
            }

        //  getGPSLocation();
    }
    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }
    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }
    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {


            case CHOOSE_SERVER_DIALOG:
                return dialogChooseServer();
            case CHECK_SERVER_DIALOG:
                return dialogAuthenticatedResponse();
            case CHANGE_SERVER_DIALOG:
                 return dialogAddServer();
            case LOAD_DIALOG:
                 return loadWebView();
            default:
                return null;
        }


    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case LOAD_DIALOG:

                ((WebViewDialog) dialog).reload(webViewURl);
        }
    }
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
                showDialog(CHOOSE_SERVER_DIALOG);
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

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }


    //threads

    private class AuthenticateThread extends Thread {
        final Handler m;
        final static int STATE_DONE = 0;
        int mState;


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
            b.putBoolean(Parameters.isLoggedIn.getText(), isLoggedIn);
            msg.setData(b);
            m.sendMessage(msg);
        }

        public void run() {

            try {
                baseURL = LocalDatabaseDaoFactory.getInstance().getSetting(StartActivity.this, Parameters.server.getText());

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
                Log.e(Android.N, e.getMessage());
                update(false);
            }


        }

    }


    //handlers

    // Define the Handler that receives messages from the thread and update the progress
    private final Handler authenticateThreadHandler = new Handler() {
        public void handleMessage(Message msg) {
            //	int total = msg.getData().getInt("total");
            final boolean isLoggedIn = msg.getData().getBoolean(Parameters.isLoggedIn.getText());
            final String cookie;
            Log.v("NimbitsV", "is logged in " + isLoggedIn);
            if (authenticateDialog != null) {
                try {
                    dismissDialog(CHECK_SERVER_DIALOG);
                    removeDialog(CHECK_SERVER_DIALOG);
                } catch (Exception e) {

                }
                authenticateThread.setState(AuthenticateThread.STATE_DONE);
                authenticateDialog = null;

                if (isLoggedIn) {

                    setContentView(R.layout.diagram_view);
                     try {

                        authCookie = OwnerAccountFactory.getInstance().getNimbitsClient(StartActivity.this, baseURL).getAuthCookie();
                        cookie= authCookie.getName() + "=" + authCookie.getValue() + "; domain=" + authCookie.getDomain();
                        if (authCookie != null) {
                            cookieManager.setCookie(baseURL,cookie );
                        }



                        // if (json != null) {
                        // final Diagram diagram = GsonFactory.getInstance().fromJson(json, DiagramModel.class);
                        mWebView = (WebView) findViewById(R.id.webview);
                        mWebView.requestFocus(View.FOCUS_DOWN);
                        mWebView.setWebViewClient(new WebViewClient() {


                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                webViewURl = url;
                                webViewcookie = cookie;


                                showDialog(LOAD_DIALOG);
                                return true;
                            }
                        });
                        mWebView.getSettings().setSupportZoom(true);

                        mWebView.getSettings().setBuiltInZoomControls(true);
                        mWebView.setInitialScale(100);
                        mWebView.getSettings().setJavaScriptEnabled(true);

                        mWebView.loadUrl(baseURL + "?" + Parameters.client.getText() + "=" + Words.WORD_ANDROID);
                    } catch (NimbitsException e) {

                    }
                } else {
                    Toast.makeText(StartActivity.this, "There was a problem connecting to Nimbits. One possibility is that Nimbits uses google accounts to authenticate. Please add a google.com (gmail.com) account to this device.", Toast.LENGTH_LONG).show();
                }
            }
        }
    };


    //dialogs

    private WebViewDialog loadWebView() {
        final WebViewDialog dialog = new WebViewDialog(this, new WebViewDialog.ReadyListener() {
            @Override
            public void ready() throws UnsupportedEncodingException, NimbitsException {

            }
        });


        return dialog;
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
                    LocalDatabaseDaoFactory.getInstance().updateSetting(StartActivity.this, Parameters.server.getText(), baseURL);
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
                urlText.setText(Path.PATH_NIMBITS_PUBLIC_SERVER);

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
        baseURL = LocalDatabaseDaoFactory.getInstance().getSetting(StartActivity.this, Parameters.server.getText());
        Log.v("NimbitsV", "Logging into " + baseURL);
        authenticateDialog = new ProgressDialog(this);
        authenticateDialog = ProgressDialog.show(this, "", "Authenticating to Nimbits Server @ " + baseURL + " using account " + OwnerAccountFactory.getInstance().getEmail(StartActivity.this) + ".  Please wait...", true);

        authenticateThread = new AuthenticateThread(authenticateThreadHandler, this);
        authenticateThread.start();
        return authenticateDialog;
    }





}