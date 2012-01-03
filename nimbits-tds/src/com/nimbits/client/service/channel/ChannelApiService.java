package com.nimbits.client.service.channel;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Point;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/1/12
 * Time: 3:38 PM
 */

@RemoteServiceRelativePath(Const.PARAM_CHANNEL)

public interface ChannelApiService extends RemoteService {

    String openChannel(final Point point);

    void notifyPointUpdated(final Point point);


}
