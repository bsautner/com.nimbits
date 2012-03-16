package com.nimbits.client.ui.helper;

import com.extjs.gxt.ui.client.widget.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 10:32 AM
 */
public class FeedbackHelper {
    public static void showError(Throwable caught) {
        final MessageBox box = MessageBox.alert("Error", caught.getMessage(), null);
        box.show();
    }
}
