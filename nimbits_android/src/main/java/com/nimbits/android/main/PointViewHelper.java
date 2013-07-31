package com.nimbits.android.main;

import android.widget.ImageView;
import android.widget.TextView;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.model.simple.SimpleValue;
import com.nimbits.cloudplatform.client.model.value.Value;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Author: Benjamin Sautner
 * Date: 1/15/13
 * Time: 10:08 AM
 */
public class PointViewHelper {

    public static void setViews(final Value v,
                                final TextView value,
                                final TextView timestamp,

                                final ImageView entityImage,
                                final SimpleValue<String> unit) {


        value.setText(String.valueOf(v.getDoubleValue()) + " " + unit.getValue());




        Calendar c = Calendar.getInstance();
        c.setTime(v.getTimestamp());

        SimpleDateFormat df = new SimpleDateFormat("h:mm:ss a  EEE, MMM d yyyy");
        String formattedDate = df.format(c.getTime());


        if (timestamp != null) {
            timestamp.setText(formattedDate);
        }
        if (entityImage != null) {

            switch (v.getAlertState()) {

                case LowAlert:
                    entityImage.setImageResource(R.drawable.bluestar);
                    break;
                case HighAlert:
                    entityImage.setImageResource(R.drawable.redstar);
                    break;
                case IdleAlert:
                    entityImage.setImageResource(R.drawable.yellowstar);
                    break;
                case OK:
                    entityImage.setImageResource(R.drawable.greenstar);
                    break;

            }
        }

    }
}
