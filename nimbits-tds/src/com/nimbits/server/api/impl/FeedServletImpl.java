/*
 * Copyright (c) 2010 Nimbits Inc.
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

package com.nimbits.server.api.impl;

import com.nimbits.client.constants.Const;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/18/12
 * Time: 12:43 PM
 */
@Transactional
@Service("feedApi")
public class FeedServletImpl  extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       // super.doPost(req, resp);

        PrintWriter out = resp.getWriter();
        out.print(Const.HTML_BOOTSTRAP);
        out.println("<h2>Nimbits Feed Message</h2>");
        out.println("<p>");
        out.print(req.getParameter("content"));
        out.println("<div class=\"row\">\n" +
                "        <p align=\"center\">\n" +
                "            <script type=\"text/javascript\"><!--\n" +
                "            google_ad_client = \"ca-pub-6491049122047226\";\n" +
                "            /* Nimbits Home */\n" +
                "            google_ad_slot = \"9838091291\";\n" +
                "            google_ad_width = 728;\n" +
                "            google_ad_height = 15;\n" +
                "            //-->\n" +
                "            </script>\n" +
                "            <script type=\"text/javascript\"\n" +
                "                    src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\">\n" +
                "            </script>\n" +
                "        </p>\n" +
                "        </div>");
        out.println("</p>");
        out.print("</body></html>");


    }

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//       // super.doGet(req, resp);
//        PrintWriter out = resp.getWriter();
//        out.print(Const.HTML_BOOTSTRAP);
//        out.print(req.getParameter("content"));
//
//        out.print("</body></html>");
//    }
}
