/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.client.io.socket;


import com.nimbits.client.model.point.Point;

/**
 * Connects java clients to a nimbits server using web sockets.
 */

public interface SocketListener extends org.eclipse.jetty.websocket.WebSocket {

    /**
     * When a subscription is processed on a point, such as a high alarm, idle alert or new value, the point and new data
     * are sent through this method via the web socket.
     *
     * The updated point
     */
    void onNotify(Point point);

    /**
     * fired whenever a point is updated and a client is requesting all updated.  Used by the heartbeat service and android clients.
     *
     *
     */
    void onUpdate(Point point);

}
