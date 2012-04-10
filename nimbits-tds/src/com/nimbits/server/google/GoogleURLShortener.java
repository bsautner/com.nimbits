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

package com.nimbits.server.google;

import com.nimbits.client.constants.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.http.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/6/12
 * Time: 2:48 PM
 */
public class GoogleURLShortener   {

    private GoogleURLShortener() {
    }

    public static String shortenURL(final String longURl) {
       final URLResource urlResource = new URLResource(longURl);
       final String json = GsonFactory.getInstance().toJson(urlResource);
       final String u = HttpCommonFactory.getInstance().doJsonPost( Path.PATH_GOOGLE_URL_SHORTENER, "",json);
       final ShortResponse response = GsonFactory.getInstance().fromJson(u, ShortResponse.class);
       return response.getId();
   }

   private static class URLResource {

       private String longUrl;

       URLResource(String longUrl) {
           this.longUrl = longUrl;
       }
   }

   private static class ShortResponse {

       private String kind;
       private String id;
       private String longUrl;

       public String getId() {
           return id;
       }
   }


}
