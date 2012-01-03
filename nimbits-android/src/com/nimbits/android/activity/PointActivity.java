package com.nimbits.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.*;
import com.nimbits.android.R;
import com.nimbits.android.account.OwnerAccountFactory;
import com.nimbits.android.dao.LocalDatabaseDaoFactory;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.gson.GsonFactory;

import java.net.URLEncoder;
import java.util.Date;


public class PointActivity extends Activity implements OnGestureListener {
// ------------------------------ FIELDS ------------------------------

    private static final int LoadDialogID = 1;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private PointName pointName;
    private String category;
    private String json;
    private Point point;

    private double lat;
    private double lng;

    private LoadThread loadThread;
    private ProgressDialog loadDialog;
    private final Handler timerHandler = new Handler();


    private GestureDetector gestureScanner;
    private String baseURL;


    private final Handler loadHandler = new Handler() {
        public void handleMessage(Message msg) {
            final byte[] chartData = msg.getData().getByteArray(Const.PARAM_CHART_DATA);
            final TextView currentValue = (TextView) findViewById(R.id.current_value);
            final TextView currentNote = (TextView) findViewById(R.id.current_note);
            final ImageView imgViewer = (ImageView) findViewById(R.id.chart_image);
            final Value rValue = (Value) msg.getData().getSerializable(Const.PARAM_VALUE);


            imgViewer.setOnClickListener(new ImageView.OnClickListener() {
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    Intent intent = new Intent();
                    b.putString(Const.PARAM_POINT, Const.PARAM_POINT);
                    b.putString(Const.PARAM_CATEGORY, category);
                    b.putString(Const.PARAM_JSON, json);
                    b.putString(Const.PARAM_BASE_URL, baseURL);

                    intent.putExtras(b);
                    intent.setClass(PointActivity.this, ChartActivity.class);
                    startActivity(intent);

                    finish();
                }
            });


            final String unit = (point != null && point.getUnit() != null) ? point.getUnit() : "";


            if (rValue != null) {
                currentValue.setText("Current Value: " + rValue.getNumberValue() + " " + unit);
                currentNote.setText("Current Note: " + rValue.getNote());
            }


            if (chartData != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(chartData, 0, chartData.length);
                imgViewer.setImageBitmap(bm);
            }

            try {
                dismissDialog(LoadDialogID);
                removeDialog(LoadDialogID);
            } catch (Exception ignored) {
            }

            timerHandler.removeCallbacks(timerTask);
            timerHandler.postDelayed(timerTask, Const.DEFAULT_TIMER_UPDATE_SPEED);
        }
    };


    private final Runnable timerTask = new Runnable() {
        public void run() {
            TextView lastUpdated = (TextView) findViewById(R.id.LastUpdated);
            lastUpdated.setText(new Date().toLocaleString());
            Log.v("point view timer", "tick");
            loadThread = new LoadThread(loadHandler);
            loadThread.start();
        }
    };

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Callback ---------------------

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            loadCategories(category);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

// --------------------- Interface OnGestureListener ---------------------

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    //	public void onShowPress(MotionEvent e) {
//
//	}
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        Log.v("action", "fling");

        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.v("action", "left");
                loadCategories(category);
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Log.v("action", "right");
                loadCategories(category);
            }
        } catch (Exception e) {
            // nothing
        }
        //		// TODO Auto-generated method stub
        return false;
    }


    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle b = getIntent().getExtras();

        baseURL = b.getString(Const.PARAM_BASE_URL);
        gestureScanner = new GestureDetector(this);

        setContentView(R.layout.point_layout);

        pointName = CommonFactoryLocator.getInstance().createPointName(b.getString(Const.PARAM_POINT));
        category = b.getString(Const.PARAM_CATEGORY);
        json = b.getString(Const.PARAM_JSON);
        this.setTitle(pointName.getValue());
        if (json != null) {
            point = GsonFactory.getInstance().fromJson(json, PointModel.class);
        }

        RadioButton radioButton = (RadioButton) findViewById(R.id.gpscheck);
        radioButton.setClickable(false);

        showDialog(LoadDialogID);
    }


    protected Dialog onCreateDialog(int id) {
        //Dialog dialog;
        switch (id) {
            case LoadDialogID:
                loadDialog = new ProgressDialog(PointActivity.this);
                loadDialog.setMessage("Loading Snapshot");
                loadThread = new LoadThread(loadHandler);
                loadThread.start();
                return loadDialog;

            default:
                loadDialog = null;
        }


        return loadDialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.point_menu, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        //mHandler.removeCallbacks(mUpdateTimeTask);
        super.onDestroy();

        //this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_point:
                saveEntry();
                return true;
            case R.id.main_menu2:
                loadCategories(null);
                return true;
//            case R.id.view_map:
//                showMap();
//                return true;
            case R.id.back_menu:
                loadCategories(category);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveEntry() {
        EditText ve = (EditText) findViewById(R.id.new_value);
        EditText ne = (EditText) findViewById(R.id.new_note);
        Double v;
        String note;


        try {
            v = Double.valueOf(ve.getText().toString());
            note = ne.getText().toString();

            TextView currentValue = (TextView) findViewById(R.id.current_value);
            TextView currentNote = (TextView) findViewById(R.id.current_note);

            currentValue.setText("Current Value: " + v);
            currentNote.setText("Current Note: " + note);


//
//			nv.setValue(v);
//			nv.setTimestamp(new Date());
//			nv.setNote(note);


            Value nv = ValueModelFactory.createValueModel(lat, lng, v, new Date(), point.getId(), note);
            String j = GsonFactory.getInstance().toJson(point);

            ContentValues update = new ContentValues();
            update.put(Const.ANDROID_COL_JSON, j);
            LocalDatabaseDaoFactory.getInstance().updatePointValuesByName(PointActivity.this, update, pointName);
            // SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(PointActivity.this).getDB(true);
            //db1 = DatabaseHelperImpl.getDB(true, PointActivity.this);
            //db1.update(Const.ANDROID_TABLE_LEVEL_TWO_DISPLAY, update, "NAME=?", new String[] {pointName});
            // db1.close();
            // PointName pointName = (PointName) CommonFactoryLocator.getInstance().createPointName()
            OwnerAccountFactory.getInstance().getNimbitsClient(PointActivity.this, baseURL).recordValue(pointName, nv);
            Toast.makeText(PointActivity.this, "Saved " + v + " '" + note + "' to " + pointName.getValue() + " using location: " + lat + "," + lng, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(PointActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

//    private void showMap() {
//        shutoffGPS();
//        Bundle b = new Bundle();
//        Intent intent = new Intent();
//        b.putString(Const.PARAM_TYPE, Const.PARAM_POINT);
//        b.putString(Const.PARAM_POINT, pointName.getValue());
//        b.putString(Const.PARAM_JSON, json);
//        b.putString(Const.PARAM_BASE_URL, baseURL);
//        intent.putExtras(b);
//        intent.setClass(PointActivity.this, MapViewActivity.class);
//        startActivity(intent);
//        finish();
//    }

    private void loadCategories(final String category) {

        Bundle b = new Bundle();
        Intent intent = new Intent();
        b.putString(Const.PARAM_RELOAD, Const.WORD_FALSE);
        b.putString(Const.PARAM_CATEGORY, category);
        b.putString(Const.PARAM_BASE_URL, baseURL);
        intent.putExtras(b);
        intent.setClass(PointActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerTask);
        }


    }


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return gestureScanner.onTouchEvent(event);
    }

    private class LoadThread extends Thread {
        final Handler m;

        @SuppressWarnings("unused")
        int mState;
        @SuppressWarnings("unused")
        int total;

        LoadThread(Handler h) {
            m = h;
        }

        private void update(byte[] chartData, Value rValue) {
            final Message msg = m.obtainMessage();
            final Bundle b = new Bundle();
            b.putByteArray(Const.PARAM_CHART_DATA, chartData);
            b.putSerializable(Const.PARAM_VALUE, rValue);
            msg.setData(b);

            m.sendMessage(msg);
        }

        public void run() {
            try {
                final Value rValue = OwnerAccountFactory.getInstance().getNimbitsClient(PointActivity.this, baseURL).getCurrentRecordedValue(pointName);

                final DisplayMetrics dm = new DisplayMetrics();

                getWindowManager().getDefaultDisplay().getMetrics(dm);

                try {
                    int w = dm.widthPixels;
                    if (w > 800) {
                        w = 800;
                    }
                    final String params = Const.PARAM_POINT + "=" + URLEncoder.encode(point.getName().getValue(), Const.CONST_ENCODING) + "&cht=lc&chs=" + w + "x200&chxt=y&autoscale=true";

                    final byte[] chartData = OwnerAccountFactory.getInstance().getNimbitsClient(PointActivity.this, baseURL).getChartImage(baseURL, params);
                    update(chartData, rValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NimbitsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }


}
