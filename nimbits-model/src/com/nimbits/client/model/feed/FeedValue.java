package com.nimbits.client.model.feed;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 12:37 PM
 */
public interface FeedValue  extends Serializable {

    String getFeedHtml();

    void setFeedHtml(String feedHtml);

    String getOriginalData();

    void setOriginalData(String originalData);
}
