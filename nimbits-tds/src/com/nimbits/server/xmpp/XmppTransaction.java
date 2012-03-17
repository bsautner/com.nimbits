package com.nimbits.server.xmpp;

import com.nimbits.client.model.point.*;
import com.nimbits.client.model.xmpp.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 1:18 PM
 */
public interface XmppTransaction  {
  void addResource(XmppResource resource);

    List<XmppResource> getPointXmppResources(Point point);
}
