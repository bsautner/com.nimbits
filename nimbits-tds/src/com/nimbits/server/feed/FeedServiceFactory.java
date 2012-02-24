package com.nimbits.server.feed;

import com.nimbits.client.service.feed.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:21 PM
 */
public class FeedServiceFactory {

    public static Feed getInstance(){
        return new FeedImpl();
    }
}
