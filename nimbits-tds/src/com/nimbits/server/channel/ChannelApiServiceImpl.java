package com.nimbits.server.channel;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.Action;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.service.channel.ChannelApiService;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/1/12
 * Time: 3:37 PM
 */
public class ChannelApiServiceImpl extends RemoteServiceServlet implements ChannelApiService, RequestCallback {


    @Override
    public void onResponseReceived(Request request, Response response) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onError(Request request, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String openChannel(Point point) {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        return channelService.createChannel(point.getUUID());

    }

    @Override
    public void notifyPointUpdated(Point point) {
        ChannelService channelService = ChannelServiceFactory.getChannelService();

        channelService.sendMessage(new ChannelMessage(point.getUUID(), Action.update.name()));
    }

}
