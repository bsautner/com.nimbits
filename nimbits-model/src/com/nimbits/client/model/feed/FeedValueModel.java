package com.nimbits.client.model.feed;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;

import java.io.*;



/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 12:58 PM
 */
public class FeedValueModel  implements Serializable, FeedValue {
    private String feedHtml;
    private String originalData;
    private int feedType;

    public FeedValueModel(final String feedHtml,final String originalData, FeedType feedType) {
        this.feedHtml = feedHtml;
        this.originalData = originalData;
        this.feedType = feedType.getCode();
    }

    private FeedValueModel() {
    }

    @Override
    public FeedType getFeedType() {
        return FeedType.get(this.feedType);
    }

    @Override
    public String getFeedHtml() {

        return feedHtml;
    }
    @Override
    public void setFeedHtml(String feedHtml) {
        this.feedHtml = feedHtml;
    }
    @Override
    public String getOriginalData() {
        return originalData;
    }
    @Override
    public void setOriginalData(String originalData) {
        this.originalData = originalData;
    }
}
