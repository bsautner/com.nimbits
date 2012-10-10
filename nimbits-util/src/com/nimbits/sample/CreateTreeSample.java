/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.sample;

import com.nimbits.client.NimbitsClient;
import com.nimbits.client.NimbitsClientFactory;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.FilterType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.user.NimbitsUser;

import static com.nimbits.user.UserFactory.createNimbitsUser;


//sample with create a folder with a point in it
//note that if you run this code twice, you'll get an error because points must have unique names, so you can't add the same one twice.


public class CreateTreeSample {

    public static void main(String[] args) throws NimbitsException {

        //String account = "bsautner@gmail.com";
        String account = "bsautner@gmail.com";
        NimbitsUser user = createNimbitsUser(account, "key!");
        NimbitsClient client = NimbitsClientFactory.getInstance(user, "http://ihealthtechnologies.appspot.com/");

       if (client.isLoggedIn()) {
           System.out.println("Connected!");
           //create an entity for the folder with the owner and parent being the top level of your account
           Entity categoryEntity = EntityModelFactory.createEntity("folder2", "My Top Level Folder", EntityType.category, ProtectionLevel.onlyMe, account, account);

           //create an actual category object, which extends entity
           Category category = CategoryFactory.createCategory(categoryEntity);

           //the response from the API is the created category object, so it has a valid ID, that ID can be the parent of whatever you want to put in it.
           Entity categoryResponse =  client.addEntity(category);

           //let's create some points! note the categoryResponse's key is being used as the parent value below. This puts the new point under the category.
           Entity pointEntity1 = EntityModelFactory.createEntity("point3", "My Point", EntityType.point, ProtectionLevel.onlyMe, categoryResponse.getKey(), account);
           String unitOfMeasure = "c"; //degrees celcius

           //a basic point, with no alerts or other settings);
           Point newPoint = PointModelFactory.createPointModel(pointEntity1, 0.0, 90, unitOfMeasure, 0.0, false, false, false, 0, false, FilterType.none, 0.0,false ,
                   PointType.basic, 0, false, 0.0);

           Entity pointResponse =  client.addEntity(newPoint);

       }
        else {
           System.out.println("Could not connect!");
       }



    }




}
