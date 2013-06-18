package com.nimbits.client.model.feed;

import com.nimbits.client.enums.FeedType;

import java.io.Serializable;



/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/28/12
 * Time: 12:58 PM
 */
public class FeedValueModel  implements Serializable, FeedValue {
    private static final long serialVersionUID = 4425524296491533685L;
    private String feedHtml = null;
    private String originalData = null;
    private int feedType = 0;

    public FeedValueModel(final String aFeedHtml,final String originalData, final FeedType feedType) {
        this.feedHtml = aFeedHtml;
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
    public void setFeedHtml(final String feedHtml) {
        this.feedHtml = feedHtml;
    }
    @Override
    public String getOriginalData() {
        return originalData;
    }
    @Override
    public void setOriginalData(final String originalData) {
        this.originalData = originalData;
    }
}
