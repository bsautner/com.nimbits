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


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.DisplayType;

public class ImageCursorAdapter extends SimpleCursorAdapter {
    private final Cursor c;
    private final Context context;

    public ImageCursorAdapter(final Context context, final int layout, final Cursor c,
                              final String[] from, final int[] to) {
        super(context, layout, c, from, to);
        this.c = c;
        this.context = context;
    }

    public View getView(final int pos, final View inView, final ViewGroup parent) {
        //Log.v("getview", "" + pos);

        View v = inView;
        if (v == null) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.main_list, null);
        }
        this.c.moveToPosition(pos);
        final String name = this.c.getString(this.c.getColumnIndex(Android.ANDROID_COL_NAME));
        final String desc = this.c.getString(this.c.getColumnIndex(Android.ANDROID_COL_DESCRIPTION));
        int type = this.c.getInt(this.c.getColumnIndex(Android.ANDROID_COL_DISPLAY_TYPE));
        DisplayType displayType = DisplayType.get(type);

        ImageView icon = (ImageView) v.findViewById(R.id.icon1);
        TextView d = (TextView) v.findViewById(R.id.text2);
        d.setText(desc);

        switch (displayType) {
            case Category: {
                icon.setImageResource(R.drawable.aquasmoothfoldersitesicon48);
                icon.setTag(Params.PARAM_CATEGORY);
                break;
            }

            case Point: {
                icon.setImageResource(R.drawable.aquaballgreenicon32);
                icon.setTag(Params.PARAM_POINT);
                break;
            }
            case HighAlarm: {
                icon.setImageResource(R.drawable.aquaballredicon32);
                icon.setTag(Params.PARAM_POINT);
                break;
            }
            case LowAlarm: {
                icon.setImageResource(R.drawable.aquaballicon32);
                icon.setTag(Params.PARAM_POINT);
                break;
            }
            case IdleAlarm: {
                icon.setImageResource(R.drawable.aquapause);
                icon.setTag(Params.PARAM_POINT);
                break;
            }
            case Diagram: {
                icon.setImageResource(R.drawable.diagram);
                icon.setTag(Params.PARAM_DIAGRAM);
                break;
            }

            default: {
                icon.setImageResource(R.drawable.diagram);
            }
        }


        TextView bTitle = (TextView) v.findViewById(R.id.text1);
        bTitle.setText(name);
        return (v);
    }
}
