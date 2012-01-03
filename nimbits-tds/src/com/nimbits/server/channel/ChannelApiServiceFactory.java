package com.nimbits.server.channel;

import com.nimbits.client.service.channel.ChannelApiService;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/1/12
 * Time: 4:33 PM
 */
public class ChannelApiServiceFactory {

    public static ChannelApiService getInstance() {
        return new ChannelApiServiceImpl();

    }

}
