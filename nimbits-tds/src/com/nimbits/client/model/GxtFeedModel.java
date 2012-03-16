package com.nimbits.client.model;

import com.extjs.gxt.ui.client.data.*;
import com.nimbits.client.model.feed.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:47 PM
 */
public class GxtFeedModel extends BaseTreeModel implements Serializable {
    private String html;


    public GxtFeedModel(FeedValue v) {
        this.html = v.getFeedHtml();
        set(Const.Params.PARAM_HTML,html);


    }

    public String getHtml() {
        return html;
    }
}
