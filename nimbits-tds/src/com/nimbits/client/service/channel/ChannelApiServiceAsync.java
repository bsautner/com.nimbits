package com.nimbits.client.service.channel;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.model.point.Point;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/1/12
 * Time: 3:40 PM
 */
public interface ChannelApiServiceAsync {


    void openChannel(final Point point, AsyncCallback<String> async);


    void notifyPointUpdated(final Point point, AsyncCallback<Void> async);
}
