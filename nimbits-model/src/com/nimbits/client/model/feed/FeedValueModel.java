package com.nimbits.client.model.feed;

import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 12:58 PM
 */
public class FeedValueModel  implements Serializable, FeedValue {
    private String feedHtml;
    private String originalData;


    public FeedValueModel( String feedHtml, String originalData) {
        this.feedHtml = feedHtml;
        this.originalData = originalData;

    }

    public FeedValueModel() {
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
