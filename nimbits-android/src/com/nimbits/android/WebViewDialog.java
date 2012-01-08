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
