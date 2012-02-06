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

package com.nimbits.server.service.impl;

import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.user.User;
import com.nimbits.server.user.UserServiceFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthTestServletImpl extends HttpServlet {
    // private static final Logger log = Logger.getLogger(AuthTestServletImpl.class.getName());
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) throws IOException {

        //  log.info("Auth Test");
        Common.addResponseHeaders(resp, ExportType.plain);
        final PrintWriter out = resp.getWriter();
        // final String verboseParam = req.getParameter(Const.PARAM_VERBOSE);
        final User u;
        try {
            u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            if (u != null && !u.isRestricted()) {
                out.print(Const.WORD_TRUE);

            } else {
                out.print(Const.WORD_FALSE);

            }
        } catch (NimbitsException e) {

            out.print(Const.WORD_FALSE);
        }
        out.close();
    }


}
