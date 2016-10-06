package com.nimbits.server.process.task;

import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

public interface ValueGeneratedListener {

    void newValue(User user, Point point, Value value);

}
