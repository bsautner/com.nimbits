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

package com.nimbits.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.nimbits.android.R;
import com.nimbits.android.dao.LocalDatabaseDaoFactory;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.diagram.DiagramModel;
import com.nimbits.server.gson.GsonFactory;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/13/11
 * Time: 12:33 PM
 */
public class DiagramActivity extends Activity implements GestureDetector.OnGestureListener {
    private WebView mWebView;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_view);
        Bundle b = getIntent().getExtras();
        final String baseURL = b.getString(Const.PARAM_BASE_URL);
        String cookie = b.getString(Const.PARAM_COOKIE);
        String diagramName = b.getString(Const.PARAM_DIAGRAM);
        //String category = b.getString(Const.PARAM_CATEGORY);
        String json = b.getString(Const.PARAM_JSON);
        this.setTitle(diagramName);
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        cookieManager.setCookie(baseURL, cookie);

        CookieSyncManager.getInstance().sync();

        if (json != null) {
            final Diagram diagram = GsonFactory.getInstance().fromJson(json, DiagramModel.class);
            mWebView = (WebView) findViewById(R.id.webview);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.contains("?" + Const.PARAM_CLIENT + "=" + Const.WORD_ANDROID)) {
                        processPointClickedResponse(url, baseURL);
                        return true;

                    } else {
                        view.loadUrl(url);
                        return true;
                    }

                }
            });
            mWebView.getSettings().setJavaScriptEnabled(true);
//            Map<String, String> headers = new HashMap<String, String>();
//            headers.put("Cookie", cookie);
            mWebView.loadUrl(baseURL + "?" + Const.PARAM_DIAGRAM + "=" + diagram.getUuid() + "&" + Const.PARAM_CLIENT + "=" + Const.WORD_ANDROID);
        }

    }

    private void processPointClickedResponse(final String url, final String baseURL) {
        String[] parts = url.split("&");
        if (parts.length > 1) {
            String s = parts[1];
            String[] x = s.split("=");
            if (x.length > 1) {
                String pointName = x[1];
                if (!(pointName == null)) {
                    final String json = LocalDatabaseDaoFactory.getInstance().getSelectedChildTableJsonByName(this, pointName);
                    final Bundle b = new Bundle();
                    final Intent intent = new Intent();
                    b.putString(Const.PARAM_CATEGORY, "");
                    b.putString(Const.PARAM_POINT, pointName);
                    b.putString(Const.PARAM_JSON, json);
                    b.putString(Const.PARAM_BASE_URL, baseURL);
                    intent.putExtras(b);
                    intent.setClass(this, PointActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
