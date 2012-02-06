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

package com.nimbits.android;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;

import java.io.UnsupportedEncodingException;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/7/12
 * Time: 11:21 AM
 */
public class WebViewDialog extends Dialog {


    private final ReadyListener readyListener;

    public WebViewDialog(final Context context,final ReadyListener readyListener) {
        super(context);

        this.readyListener = readyListener;


    }
    public interface ReadyListener {
        public void ready( ) throws UnsupportedEncodingException, NimbitsException;
    }

    public void reload(final String pointUrl) {
        WebView view = (WebView) findViewById(R.id.webview1);
        view.loadUrl(pointUrl);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        Button buttonOK = (Button) findViewById(R.id.Button01);
        buttonOK.setOnClickListener(new OKListener());
        WebView view = (WebView) findViewById(R.id.webview1);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setSupportZoom(true);
        view.getSettings().setBuiltInZoomControls(true);
        view.setInitialScale(100);
        view.getSettings().setJavaScriptEnabled(true);

    }

    private class OKListener implements android.view.View.OnClickListener {
        public void onClick(View v) {


            try {
                try {
                    readyListener.ready();
                } catch (NimbitsException e) {

                }
            } catch (UnsupportedEncodingException e) {
                Log.e(Const.N, e.getMessage());
            }
            reload("about:blank");
            WebViewDialog.this.dismiss();
        }
    }
}
