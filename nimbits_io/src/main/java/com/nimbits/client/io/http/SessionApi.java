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

package com.nimbits.client.io.http;

import com.nimbits.client.model.user.User;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface SessionApi {

    final String SESSION_API = "/service/v2/session";

    @POST(SESSION_API)
    User login(@Query("email") String email, @Query("password") String key);

    @POST(SESSION_API)
    User login();

    @GET(SESSION_API)
    User getSession();


}
