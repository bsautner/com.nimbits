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

package com.nimbits.server.api;

import com.google.gson.annotations.Expose;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.value.Value;

import java.util.List;

public class JitModel {

    @Expose
    public String id;
    @Expose
    public String name;
    @Expose
    public Object data;
    @Expose
    public List<JitModel> children;

    public JitModel(Entity entity, Value value, String id, String name, Object data, List<JitModel> children) {
        this.id = id;
        StringBuilder builder = new StringBuilder();
        if (entity.getEntityType().equals(EntityType.point)) {
            builder.append(name.replace(entity.getOwner() + "/", ""));
        } else {
            builder.append(name);
        }
        builder.append(" ").append(value.getValueWithData());
        this.name = builder.toString();
        this.data = data;
        this.children = children;
    }

    public JitModel(Entity entity, String id, String name, Object data, List<JitModel> children) {
        this.id = id;
        StringBuilder builder = new StringBuilder();
        builder.append(name);


        this.name = builder.toString();
        this.data = data;
        this.children = children;
    }
}
