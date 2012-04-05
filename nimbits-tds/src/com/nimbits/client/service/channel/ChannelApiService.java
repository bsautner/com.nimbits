/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.channel;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.point.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/1/12
 * Time: 3:38 PM
 */

@RemoteServiceRelativePath("channel")

public interface ChannelApiService extends RemoteService {

    String openChannel(final Point point);

    void notifyPointUpdated(final Point point);


}
