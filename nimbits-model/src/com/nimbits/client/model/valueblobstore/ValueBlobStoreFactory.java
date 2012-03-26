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

package com.nimbits.client.model.valueblobstore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/23/12
 * Time: 10:50 AM
 */
public class ValueBlobStoreFactory {

    public static ValueBlobStore createValueBlobStore(ValueBlobStore store) {
        return new ValueBlobStoreModel(store);

    }
    public static  List<ValueBlobStore> createValueBlobStores(List<ValueBlobStore> store) {
       List<ValueBlobStore> retObj = new ArrayList<ValueBlobStore>();

      for (ValueBlobStore v : store) {
        retObj.add(createValueBlobStore(v));
      }
        return retObj;

    }
}
