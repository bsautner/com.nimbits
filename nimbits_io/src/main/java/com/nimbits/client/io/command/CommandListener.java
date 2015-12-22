package com.nimbits.client.io.command;

import com.nimbits.client.model.entity.Entity;

import java.util.List;

public interface CommandListener {

    void onMessage(String message);

    void setCurrent(Entity user);

    void onTreeUpdated(List<Entity> tree);
}
