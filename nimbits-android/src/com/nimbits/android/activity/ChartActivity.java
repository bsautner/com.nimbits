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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageView;
import com.nimbits.android.R;
import com.nimbits.android.account.OwnerAccountFactory;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.server.gson.GsonFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class ChartActivity extends Activity implements OnGestureListener {


    private String pointlist = "";

    private String category = "";
    private String chartTitle = "";
    private GestureDetector gestureScanner;
    private String baseURL;
    private static final int timerInterval = 15000;

    private static final int LoadDialogID = 1;
    private LoadThread loadThread;
    private ProgressDialog loadDialog;
    private final Handler timerHandler = new Handler();
    private String type;
    private String json;


    private final Runnable timerTask = new Runnable() {
        public void run() {
            Log.v("chart view timer", "tick");
            loadThread = new LoadThread(loadHandler);
            loadThread.start();
        }
    };

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;

        switch (id) {
            case LoadDialogID:
                loadDialog = new ProgressDialog(ChartActivity.this);
                loadDialog.setMessage("Loading Chart");
                loadThread = new LoadThread(loadHandler);
                loadThread.start();
                return loadDialog;
            default:
                dialog = null;
        }


        return dialog;
    }

    private final Handler loadHandler = new Handler() {


        public void handleMessage(Message msg) {
            byte[] chartData = msg.getData().getByteArray(Const.PARAM_CHART_DATA);
            ImageView imgViewer = (ImageView) findViewById(R.id.chart_image);
            Bitmap bm = BitmapFactory.decodeByteArray(chartData, 0, chartData.length);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            imgViewer.setMinimumHeight(dm.heightPixels);
            imgViewer.setMinimumWidth(dm.widthPixels);
            imgViewer.setImageBitmap(bm);

            try {
                dismissDialog(LoadDialogID);
                removeDialog(LoadDialogID);
            } catch (Exception e) {
                Log.e("error", e.getMessage(), e);
            }

            timerHandler.removeCallbacks(timerTask);
            timerHandler.postDelayed(timerTask, timerInterval);

        }
    };

    private class LoadThread extends Thread {
        final Handler m;
        final static int STATE_DONE = 0;
        final static int STATE_RUNNING = 1;
        @SuppressWarnings("unused")
        int mState;
        @SuppressWarnings("unused")
        int total;

        LoadThread(Handler h) {
            m = h;
        }

        private void update(byte[] chartData) {
            final Message msg = m.obtainMessage();
            final Bundle b = new Bundle();
            b.putByteArray(Const.PARAM_CHART_DATA, chartData);
            msg.setData(b);
            m.sendMessage(msg);
        }

        //TODO cleanup
        public void run() {
            byte[] chartData = null;
            try {
                if (type.equals(Const.PARAM_POINT)) {

                    if (json != null) {

                        final Point point = GsonFactory.getInstance().fromJson(json, PointModel.class);
                        pointlist = URLEncoder.encode(point.getName().getValue(), Const.CONST_ENCODING);
                        chartTitle = URLEncoder.encode(point.getName().getValue(), Const.CONST_ENCODING);
                    }


                } else {

                    final List<Point> points = GsonFactory.getInstance().fromJson(json, GsonFactory.pointListType);
                    chartTitle = URLEncoder.encode(category, Const.CONST_ENCODING);

                    if (points.size() > 0) {
                        for (final Point p : points) {
                            pointlist += URLEncoder.encode(p.getName().getValue(), Const.CONST_ENCODING) + "%2C";
                        }
                        pointlist = StringUtils.removeEnd(pointlist, "%2C");
//                                pointlist.substring(0, pointlist.length() - 1);

                    }

                }
            } catch (UnsupportedEncodingException e) {
                Log.e("error", e.getMessage(), e);
            }

            Log.v("chartact points", pointlist);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);


            String params = "points=" + pointlist +
                    "&cht=lc&chs=" + 500 + "x" + 600 +
                    "&chxt=y&autoscale=true" +
                    "&chco=000000,00FF00,0000FF,FF0000,FF0066,FFCC33,663333,003333" +
                    "&chtt=" + chartTitle +
                    "&chdl=" + pointlist.replace("%2C", "%7C");


            try {

                chartData = OwnerAccountFactory.getInstance().getNimbitsClient(ChartActivity.this, baseURL).getChartImage(baseURL, params);
            } catch (Exception e) {
                Log.e("error", e.getMessage(), e);
            }


            update(chartData);

        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chart_layout);
        gestureScanner = new GestureDetector(this);
        Bundle b = getIntent().getExtras();

        json = b.getString(Const.PARAM_JSON);
        category = b.getString(Const.PARAM_CATEGORY);
        baseURL = b.getString(Const.PARAM_BASE_URL);
        type = b.getString(Const.PARAM_TYPE);

        showDialog(LoadDialogID);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            loadCategories();
            return true;


        }

        return super.onKeyDown(keyCode, event);
    }

    private void loadCategories() {
        final Bundle b = new Bundle();
        final Intent intent = new Intent();
        b.putBoolean(Const.PARAM_RELOAD, false);
        b.putString(Const.PARAM_CATEGORY, category);
        b.putString(Const.PARAM_BASE_URL, baseURL);
        intent.putExtras(b);
        intent.setClass(ChartActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.main_menu:
                loadCategories();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        this.finish();
    }


    @Override
    public void finish() {
        super.finish();
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerTask);
        }
    }


    public void onTerminate() {

        // clean up application global

        //super.onStop();

        super.onDestroy();

        this.finish();          // /// close the application

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return gestureScanner.onTouchEvent(event);

    }

    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub


        loadCategories();


        return false;
    }

    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }
}
