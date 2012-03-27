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

   public static String shortenURL(final String longURl) {
       final URLResource urlResource = new URLResource(longURl);
       final String json = GsonFactory.getInstance().toJson(urlResource);
       final String u = HttpCommonFactory.getInstance().doJsonPost( Path.PATH_GOOGLE_URL_SHORTENER, "",json);
       final ShortResponse response = GsonFactory.getInstance().fromJson(u, ShortResponse.class);
       return response.getId();
   }

   private static class URLResource {

       private String longUrl;

       private URLResource(String longUrl) {
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
