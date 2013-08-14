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

package com.nimbits.cloudplatform.client.model.feed;

import com.nimbits.cloudplatform.client.enums.FeedType;

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
