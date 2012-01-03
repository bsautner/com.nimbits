package com.nimbits.android;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/2/11
 * Time: 1:03 PM
 */
public class DataEntryDialog extends Dialog {
    public interface RecordValueListener {
        public void ready(Bundle bundle, Value value) throws UnsupportedEncodingException;
    }

    public interface ChartRequestListener {
        public void ready(Bundle bundle) throws UnsupportedEncodingException;
    }


    private final RecordValueListener recordValueListener;
    private final ChartRequestListener chartRequestListener;
    private static final int LoadDialogID = 1;
    private EditText valueEntry;
    private EditText noteEntry;
    private Bundle bundle;
    private double lat;
    private double lng;
    private PointName pointName;
    private Context context;
    private Point point;
    private String base;
    private LoadThread loadThread;

    public DataEntryDialog(Context context,
                           Bundle bundle1,
                           RecordValueListener okListener,
                           ChartRequestListener viewChartListener,
                           double lat, double lng) {
        super(context);
        // this.name = name;
        this.lat = lat;
        this.lat = lng;
        this.bundle = bundle1;
        this.recordValueListener = okListener;
        this.chartRequestListener = viewChartListener;
        this.context = context;
        pointName = CommonFactoryLocator.getInstance().createPointName(bundle.getString(Const.PARAM_POINT));
        String json = bundle1.getString(Const.PARAM_JSON);
        base = bundle.getString(Const.PARAM_BASE_URL);
        if (json != null) {
            point = GsonFactory.getInstance().fromJson(json, PointModel.class);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_dialog);
        setTitle(pointName.getValue());

        Button buttonSave = (Button) findViewById(R.id.Button02);

        Button buttonClose = (Button) findViewById(R.id.Button01);

        buttonSave.setOnClickListener(new OKListener());

        buttonClose.setOnClickListener(new CloseListener());

        valueEntry = (EditText) findViewById(R.id.EditText01);
        noteEntry = (EditText) findViewById(R.id.EditText02);
        loadThread = new LoadThread(loadHandler);
        loadThread.start();

    }

    private final Handler loadHandler = new Handler() {


        public void handleMessage(Message msg) {
            byte[] chartData = msg.getData().getByteArray(Const.PARAM_CHART_DATA);
            ImageView imgViewer = (ImageView) findViewById(R.id.chart_image);
            Bitmap bm = BitmapFactory.decodeByteArray(chartData, 0, chartData.length);


            imgViewer.setImageBitmap(bm);
            imgViewer.setClickable(true);
            imgViewer.setOnClickListener(new ViewChartListener());


        }
    };

    private class CloseListener implements android.view.View.OnClickListener {

        @Override
        public void onClick(View view) {
            DataEntryDialog.this.dismiss();
            try {
                recordValueListener.ready(bundle, null);
            } catch (UnsupportedEncodingException e) {

            }
        }
    }

    private class OKListener implements android.view.View.OnClickListener {
        public void onClick(View v) {


            try {
                String vs = String.valueOf(valueEntry.getText());

                String note = String.valueOf(noteEntry.getText());
                double value;
                try {
                    value = Double.parseDouble(vs);
                } catch (NumberFormatException e) {
                    value = 0.0;

                }

                if (StringUtils.isNotEmpty(vs) || StringUtils.isNotEmpty(note)) {
                    Value nv = ValueModelFactory.createValueModel(lat, lng, value, new Date(), 0, note, "");

                    String j = GsonFactory.getInstance().toJson(point);

                    ContentValues update = new ContentValues();
                    update.put(Const.ANDROID_COL_JSON, j);
                    LocalDatabaseDaoFactory.getInstance().updatePointValuesByName(context, update, pointName);


                    // SQLiteDatabase db1 = DatabaseHelperFactory.getInstance(PointActivity.this).getDB(true);
                    //db1 = DatabaseHelperImpl.getDB(true, PointActivity.this);
                    //db1.update(Const.ANDROID_TABLE_LEVEL_TWO_DISPLAY, update, "NAME=?", new String[] {pointName});
                    // db1.close();
                    // PointName pointName = (PointName) CommonFactoryLocator.getInstance().createPointName()
                    OwnerAccountFactory.getInstance().getNimbitsClient(context, base).recordValue(pointName, nv);
                    Toast.makeText(context, "Saved " + value + " '" + note + "' to " + pointName.getValue() + " using location: " + lat + "," + lng, Toast.LENGTH_LONG).show();
                    recordValueListener.ready(bundle, nv);
                    DataEntryDialog.this.dismiss();
                } else {
                    Toast.makeText(context, "Nothing saved, please enter a value or cancel", Toast.LENGTH_LONG).show();
                }

            } catch (UnsupportedEncodingException e) {
                Log.e(Const.N, e.getMessage());
            } catch (IOException e) {
                Log.e(Const.N, e.getMessage());
            } catch (NimbitsException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    private class ViewChartListener implements android.view.View.OnClickListener {
        public void onClick(View v) {


            try {


                chartRequestListener.ready(bundle);
            } catch (UnsupportedEncodingException e) {
                Log.e(Const.N, e.getMessage());
            }
            DataEntryDialog.this.dismiss();
        }
    }

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


            String params = "points=" + pointName.getValue() +
                    "&cht=ls&chs=200x200&chf=bg,s,000000&chco=FFFF00";


            try {

                chartData = OwnerAccountFactory.getInstance().getNimbitsClient(context, base).getChartImage(base, params);
            } catch (Exception e) {
                Log.e("error", e.getMessage(), e);
            }


            update(chartData);


        }
    }
}
