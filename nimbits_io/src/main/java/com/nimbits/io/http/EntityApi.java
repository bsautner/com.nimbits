/*
 * NIMBITS INC CONFIDENTIAL
 *  __________________
 *
 * [2013] - [2014] Nimbits Inc
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Nimbits Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Nimbits Inc
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Nimbits Inc.
 */

package com.nimbits.io.http;


import com.nimbits.client.model.entity.Entity;
import retrofit.http.GET;
import retrofit.http.Query;

import java.util.List;

public interface EntityApi {

    final String API ="/service/v2/entity";


    @GET(API)
    List<Entity> getEntity(@Query("email") String email, @Query("key") String key, @Query("id") String id);


}
