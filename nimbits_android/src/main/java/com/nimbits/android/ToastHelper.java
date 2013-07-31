package com.nimbits.android;

import android.content.Context;
import android.widget.Toast;

/**
 * Author: Benjamin Sautner
 * Date: 1/8/13
 * Time: 2:17 PM
 */
public class ToastHelper {

    public static void show(Context activity, String message) {

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(activity, message, duration);
        toast.show();

    }
}
