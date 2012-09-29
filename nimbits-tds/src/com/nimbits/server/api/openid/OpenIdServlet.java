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

package com.nimbits.server.api.openid;


import com.google.step2.AuthRequestHelper;
import com.google.step2.AuthResponseHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.openid.ui.UiMessageRequest;
import org.apache.commons.lang.StringUtils;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling OpenID logins.  Uses the Step2 library from code.google.com and the
 * underlying OpenID4Java library.
 */
@Service("openId")
@Transactional
public class OpenIdServlet extends HttpServlet implements org.springframework.web.HttpRequestHandler {

    protected ConsumerHelper consumerHelper;
    protected String realm;
    protected String returnToPath;
    protected String homePath;

    /**
     * Init the servlet.  For demo purposes, we're just using an in-memory version
     * of OpenID4Java's ConsumerAssociationStore.  Production apps, particularly those
     * in a clustered environment, should consider using an implementation backed by
     * shared storage (memcache, DB, etc.)
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        init();

    }

    @Override
    public void init() throws ServletException {
        super.init();



        returnToPath ="/openid";

        realm = null;
        homePath =  "/?hd=com";
        ConsumerFactory factory = new ConsumerFactory(
                new InMemoryConsumerAssociationStore());
        consumerHelper = factory.getConsumerHelper();
    }


    AuthRequest startAuthentication(String op, HttpServletRequest request)
            throws OpenIDException {
        IdpIdentifier openId = new IdpIdentifier(op);

        String realm = realm(request);
        String returnToUrl = returnTo(request);

        AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(openId,
                returnToUrl);
        addAttributes(helper);

        HttpSession session = request.getSession();
        AuthRequest authReq = helper.generateRequest();
        authReq.setRealm(realm);

        UiMessageRequest uiExtension = new UiMessageRequest();
        uiExtension.setIconRequest(true);
        authReq.addExtension(uiExtension);

        session.setAttribute("discovered", helper.getDiscoveryInformation());
        return authReq;
    }

    /**
     * Validates the response to an auth request, returning an authenticated user object if
     * successful.
     *
     * @param request Current servlet request
     * @return User
     * @throws org.openid4java.OpenIDException if unable to verify response
     */

    UserInfo completeAuthentication(HttpServletRequest request)
            throws OpenIDException {
        HttpSession session = request.getSession();
        ParameterList openidResp = Step2.getParameterList(request);
        String receivingUrl = currentUrl(request);
        DiscoveryInformation discovered = (DiscoveryInformation) session
                .getAttribute("discovered");

        AuthResponseHelper authResponse = consumerHelper.verify(receivingUrl,
                openidResp, discovered);

        if (authResponse.getAuthResultType() == AuthResponseHelper.ResultType.AUTH_SUCCESS) {
            return onSuccess(authResponse, request);
        }
        return onFail(authResponse, request);
    }

    /**
     * Adds the requested AX attributes to the request
     *
     * @param helper Request builder
     */
    void addAttributes(AuthRequestHelper helper) {
        helper.requestAxAttribute(Step2.AxSchema.EMAIL, true)
                .requestAxAttribute(Step2.AxSchema.FIRST_NAME, true)
                .requestAxAttribute(Step2.AxSchema.LAST_NAME, true);
    }

    /**
     * Reconstructs the current URL of the request, as sent by the user
     *
     * @param request Current servlet request
     * @return URL as sent by user
     */
    String currentUrl(HttpServletRequest request) {
        return Step2.getUrlWithQueryString(request);
    }

    /**
     * Gets the realm to advertise to the IDP.  If not specified in the servlet configuration.
     * it dynamically constructs the realm based on the current request.
     *
     * @param request Current servlet request
     * @return Realm
     */
    String realm(HttpServletRequest request) {
        if (StringUtils.isNotBlank(realm)) {
            return realm;
        } else {
            return baseUrl(request);
        }
    }

    /**
     * Gets the <code>openid.return_to</code> URL to advertise to the IDP.  Dynamically constructs
     * the URL based on the current request.
     * @param request Current servlet request
     * @return Return to URL
     */
    String returnTo(HttpServletRequest request) {
        return baseUrl(request) + request.getContextPath() + returnToPath;
    }

    /**
     * Dynamically constructs the base URL for the application based on the current request
     *
     * @param request Current servlet request
     * @return Base URL (path to servlet context)
     */
    String baseUrl(HttpServletRequest request) {
        StringBuffer url = new StringBuffer(request.getScheme()).append("://")
                .append(request.getServerName());

        if ((request.getScheme().equalsIgnoreCase("http") && request
                .getServerPort() != 80)
                || (request.getScheme().equalsIgnoreCase("https") && request
                .getServerPort() != 443)) {
            url.append(":").append(request.getServerPort());
        }

        return url.toString();
    }

    /**
     * Map the OpenID response into a user for our app.
     *
     * @param helper Auth response
     * @param request Current servlet request
     * @return User representation
     */
    UserInfo onSuccess(AuthResponseHelper helper, HttpServletRequest request) {

        return new UserInfo(helper.getClaimedId().toString(),
                helper.getAxFetchAttributeValue(Step2.AxSchema.EMAIL),
                helper.getAxFetchAttributeValue(Step2.AxSchema.FIRST_NAME),
                helper.getAxFetchAttributeValue(Step2.AxSchema.LAST_NAME));
    }

    /**
     * Handles the case where authentication failed or was canceled.  Just a no-op
     * here.
     *
     * @param helper Auth response
     * @param request Current servlet request
     * @return User representation
     */
    UserInfo onFail(AuthResponseHelper helper, HttpServletRequest request) {
        return null;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (consumerHelper==null) {
            init();
        }


        String domain = req.getParameter("hd");
        if (domain != null) {
            // User attempting to login with provided domain, build and OpenID request and redirect
            try {
                AuthRequest authRequest = startAuthentication(domain, req);
                String url = authRequest.getDestinationUrl(true);
                resp.sendRedirect(url);
            } catch (OpenIDException e) {
                throw new ServletException("Error initializing OpenID request", e);
            }
        } else {
            // This is a response from the provider, go ahead and validate
            doPost(req, resp);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            UserInfo user = completeAuthentication(req);
            req.getSession().setAttribute("user", user);
            resp.sendRedirect(homePath);
        } catch (OpenIDException e) {
            throw new ServletException("Error processing OpenID response", e);
        }
    }

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (isPost(req)) {

            doPost(req, resp);
        }
        else {
            doGet(req, resp);
        }

    }
    protected boolean isPost(final HttpServletRequest req) {
        return req.getMethod().equals("POST");
    }

}
