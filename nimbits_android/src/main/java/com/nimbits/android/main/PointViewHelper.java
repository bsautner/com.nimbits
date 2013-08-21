/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.android.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nimbits.android.R;
import com.nimbits.cloudplatform.client.constants.Const;
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

      //  Value v = point.getValue();
        value.setText(String.valueOf(v.getDoubleValue()) + " " + unit.getValue());
        value.setVisibility(View.VISIBLE);
        timestamp.setVisibility(View.VISIBLE);

        if (v.getDoubleValue() == Const.CONST_IGNORED_NUMBER_VALUE) {
            value.setVisibility(View.GONE);
            timestamp.setVisibility(View.GONE);
            return;
        }

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
