/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.relationship;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/4/12
 * Time: 9:49 AM
 */
public class RelationshipFactory {

    protected RelationshipFactory() {

    }

    public static Relationship createRelationship(Relationship relationship) {

        return new RelationshipModel(relationship);

    }
    public static List<Relationship> createRelationships(List<Relationship> relationships) {
        List<Relationship> retObj = new ArrayList<Relationship>(relationships.size());
        for (Relationship r : relationships) {
            retObj.add(createRelationship(r));
        }
        return retObj;

    }


}
