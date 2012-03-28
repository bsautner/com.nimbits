package com.nimbits.server.api;

import com.google.appengine.tools.development.testing.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.api.impl.*;
import com.nimbits.server.user.*;
import org.junit.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:52 PM
 */
public class ValueServletImplTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    UserTransactions dao;
    @Before
    public void setUp() throws NimbitsException {

        helper.setUp();

        dao = UserTransactionFactory.getDAOInstance();
        dao.createNimbitsUser(CommonFactoryLocator.getInstance().createEmailAddress("test@example.com"));
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }



    @Test
    public void doGetTest() throws IOException, NimbitsException {
        String email = "test@test.com";
        EmailAddress em = CommonFactoryLocator.getInstance().createEmailAddress(email);


        dao.createNimbitsUser(em);
        User user = dao.getNimbitsUser(em);


        ValueServletImpl s = new ValueServletImpl();
        HttpServletRequest req = new HttpServletRequest() {
            @Override
            public String getAuthType() {
              return "";

            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];  //auto generated
            }

            @Override
            public long getDateHeader(String s) {
                return 0;  //auto generated
            }

            @Override
            public String getHeader(String s) {
                return null;  //auto generated
            }

            @Override
            public Enumeration getHeaders(String s) {
                return null;  //auto generated
            }

            @Override
            public Enumeration getHeaderNames() {
                return null;  //auto generated
            }

            @Override
            public int getIntHeader(String s) {
                return 0;  //auto generated
            }

            @Override
            public String getMethod() {
                return null;  //auto generated
            }

            @Override
            public String getPathInfo() {
                return null;  //auto generated
            }

            @Override
            public String getPathTranslated() {
                return null;  //auto generated
            }

            @Override
            public String getContextPath() {
                return null;  //auto generated
            }

            @Override
            public String getQueryString() {
                return "email=test@example.com";
            }

            @Override
            public String getRemoteUser() {
                return null;  //auto generated
            }

            @Override
            public boolean isUserInRole(String s) {
                return false;  //auto generated
            }

            @Override
            public Principal getUserPrincipal() {
                return null;  //auto generated
            }

            @Override
            public String getRequestedSessionId() {
                return null;  //auto generated
            }

            @Override
            public String getRequestURI() {
                return null;  //auto generated
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;  //auto generated
            }

            @Override
            public String getServletPath() {
                return null;  //auto generated
            }

            @Override
            public HttpSession getSession(boolean b) {
                return null;  //auto generated
            }

            @Override
            public HttpSession getSession() {
                return null;  //auto generated
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;  //auto generated
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;  //auto generated
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;  //auto generated
            }

            @Override
            public boolean isRequestedSessionIdFromUrl() {
                return false;  //auto generated
            }

            @Override
            public Object getAttribute(String s) {
                return null;  //auto generated
            }

            @Override
            public Enumeration getAttributeNames() {
                return null;  //auto generated
            }

            @Override
            public String getCharacterEncoding() {
                return null;  //auto generated
            }

            @Override
            public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
                //auto generated
            }

            @Override
            public int getContentLength() {
                return 0;  //auto generated
            }

            @Override
            public String getContentType() {
                return null;  //auto generated
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                return null;  //auto generated
            }

            @Override
            public String getParameter(String s) {

                return "Test";

            }

            @Override
            public Enumeration getParameterNames() {
                return null;  //auto generated
            }

            @Override
            public String[] getParameterValues(String s) {
                return new String[0];  //auto generated
            }

            @Override
            public Map getParameterMap() {
                return null;  //auto generated
            }

            @Override
            public String getProtocol() {
                return null;  //auto generated
            }

            @Override
            public String getScheme() {
                return null;  //auto generated
            }

            @Override
            public String getServerName() {
                return null;  //auto generated
            }

            @Override
            public int getServerPort() {
                return 0;  //auto generated
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return null;  //auto generated
            }

            @Override
            public String getRemoteAddr() {
                return null;  //auto generated
            }

            @Override
            public String getRemoteHost() {
                return null;  //auto generated
            }

            @Override
            public void setAttribute(String s, Object o) {
                //auto generated
            }

            @Override
            public void removeAttribute(String s) {
                //auto generated
            }

            @Override
            public Locale getLocale() {
                return null;  //auto generated
            }

            @Override
            public Enumeration getLocales() {
                return null;  //auto generated
            }

            @Override
            public boolean isSecure() {
                return false;  //auto generated
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String s) {
                return null;  //auto generated
            }

            @Override
            public String getRealPath(String s) {
                return null;  //auto generated
            }

            @Override
            public int getRemotePort() {
                return 0;  //auto generated
            }

            @Override
            public String getLocalName() {
                return null;  //auto generated
            }

            @Override
            public String getLocalAddr() {
                return null;  //auto generated
            }

            @Override
            public int getLocalPort() {
                return 0;  //auto generated
            }
        };
        HttpServletResponse resp = new HttpServletResponse() {
            @Override
            public void addCookie(Cookie cookie) {
                //auto generated
            }

            @Override
            public boolean containsHeader(String s) {
                return false;  //auto generated
            }

            @Override
            public String encodeURL(String s) {
                return null;  //auto generated
            }

            @Override
            public String encodeRedirectURL(String s) {
                return null;  //auto generated
            }

            @Override
            public String encodeUrl(String s) {
                return null;  //auto generated
            }

            @Override
            public String encodeRedirectUrl(String s) {
                return null;  //auto generated
            }

            @Override
            public void sendError(int i, String s) throws IOException {
                //auto generated
            }

            @Override
            public void sendError(int i) throws IOException {
                //auto generated
            }

            @Override
            public void sendRedirect(String s) throws IOException {
                //auto generated
            }

            @Override
            public void setDateHeader(String s, long l) {
                //auto generated
            }

            @Override
            public void addDateHeader(String s, long l) {
                //auto generated
            }

            @Override
            public void setHeader(String s, String s1) {
                //auto generated
            }

            @Override
            public void addHeader(String s, String s1) {
                //auto generated
            }

            @Override
            public void setIntHeader(String s, int i) {
                //auto generated
            }

            @Override
            public void addIntHeader(String s, int i) {
                //auto generated
            }

            @Override
            public void setStatus(int i) {
                //auto generated
            }

            @Override
            public void setStatus(int i, String s) {
                //auto generated
            }

            @Override
            public String getCharacterEncoding() {
                return null;  //auto generated
            }

            @Override
            public String getContentType() {
                return null;  //auto generated
            }

            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return null;  //auto generated
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return null;  //auto generated
            }

            @Override
            public void setCharacterEncoding(String s) {
                //auto generated
            }

            @Override
            public void setContentLength(int i) {
                //auto generated
            }

            @Override
            public void setContentType(String s) {
                //auto generated
            }

            @Override
            public void setBufferSize(int i) {
                //auto generated
            }

            @Override
            public int getBufferSize() {
                return 0;  //auto generated
            }

            @Override
            public void flushBuffer() throws IOException {
                //auto generated
            }

            @Override
            public void resetBuffer() {
                //auto generated
            }

            @Override
            public boolean isCommitted() {
                return false;  //auto generated
            }

            @Override
            public void reset() {
                //auto generated
            }

            @Override
            public void setLocale(Locale locale) {
                //auto generated
            }

            @Override
            public Locale getLocale() {
                return null;  //auto generated
            }
        };


        s.doGet(req, resp);



    }
}
