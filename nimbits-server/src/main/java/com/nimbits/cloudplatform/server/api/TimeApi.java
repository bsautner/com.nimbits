package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.server.gson.GsonFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Author: Benjamin Sautner
 * Date: 2/2/13
 * Time: 12:08 PM
 */

@Service("timeApi")
public class TimeApi  implements org.springframework.web.HttpRequestHandler {


    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final PrintWriter out = resp.getWriter();

        Long time = new Date().getTime();
        String reponse = GsonFactory.getInstance().toJson(time);
        out.print(reponse);
        out.close();

    }
}
