package com.nimbits.client.model;

import com.extjs.gxt.ui.client.data.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:47 PM
 */
public class GxtValueModel  extends BaseTreeModel implements Serializable {

    public GxtValueModel() {
        set("title", "hello workd");
        set("name", "my name");


    }
}
