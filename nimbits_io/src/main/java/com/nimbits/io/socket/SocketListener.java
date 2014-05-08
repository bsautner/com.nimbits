package com.nimbits.io.socket;


import com.nimbits.client.model.point.Point;

/**
 * Connects java clients to a nimbits server using web sockets.
 *
 */

public interface SocketListener extends org.eclipse.jetty.websocket.WebSocket {

    /**
     * When a subscription is processed on a point, such as a high alarm, idle alert or new value, the point and new data
     * are sent through this method via the web socket.
     * @return The updated point
     *
     *
     */
    void onNotify(Point point);

    /**
     * fired whenever a point is updated and a client is requesting all updated.  Used by the heartbeat service and android clients.
     * @return
     */
    void onUpdate(Point point);

}
