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

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/21/11
 * Time: 11:10 AM
 */


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A servlet which handles the OAuth 2.0 flow. Once authenticated, the OAuth token is stored in session.accessToken.
 *
 * @author Jenny Murphy
 */
@SuppressWarnings("serial")
public class OAuth2Servlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(OAuth2Servlet.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        HttpSession session = req.getSession();
//        String CLIENT_ID = "768124221699@developer.gserviceaccount.com";
//        String CLIENT_SECRET = "fFkt6n3WiO7r4856I4qB1dHJ";
//        String REDIRECT_URI = "http://app.nimbits.com/oauth2callback";
//        String SCOPES = "https://www.google.com/m8/feeds/";
//
//      //  Check for an error returned by OAuth
//    String error = req.getParameter("error");
//    if (error != null) {
//      resp.setContentType("text/plain");
//      resp.getWriter().println("There was a problem during authentication: " + error);
//      log.severe("There was a problem during authentication: " + error);
//      return;
//    }
//
//    // When we're redirected back from the OAuth 2.0 grant page, a code will be supplied in a GET parameter named 'code'
//    String code = req.getParameter("code");
//    if (code == null || code.isEmpty()) {
//      // Now that we have the OAuth 2.0 code, we must exchange it for a token to make API requests.
//
//
//      // Build the authorization URL
//      AuthorizationRequestUrl authorizeUrl = new GoogleAuthorizationRequestUrl(
//              CLIENT_ID,
//               REDIRECT_URI,
//               SCOPES
//      );
//      authorizeUrl.redirectUri =  REDIRECT_URI;
//      authorizeUrl.scope =  SCOPES;
//      String authorizationUrl = authorizeUrl.build();
//
//      log.info("Redirecting browser for OAuth 2.0 authorization to " + authorizationUrl);
//      resp.sendRedirect(authorizationUrl);
//      return;
//    } else {
//      log.info("Exchanging OAuth code for access token using server side call");
//
//      AccessTokenResponse accessTokenResponse = new GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant(
//
//              new NetHttpTransport(),
//              new GsonFactory(),
//               CLIENT_ID,
//               CLIENT_SECRET,
//              code,
//               REDIRECT_URI
//      ).execute();
//
//      log.info("Storing authentication token into the session");
//      session.setAttribute("accessToken", accessTokenResponse.accessToken);
//      session.setAttribute("refreshToken", accessTokenResponse.refreshToken);
//
//      //The authentication is all done! Redirect back to the samples index so you can play with them.
//      resp.sendRedirect("/");
////    }
//    }
    }
}

