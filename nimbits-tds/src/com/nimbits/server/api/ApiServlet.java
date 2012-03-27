package com.nimbits.server.api;

import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/20/12
 * Time: 12:58 PM
 */
public class ApiServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ApiServlet.class.getName());
    protected User user;
    private Map<ApiParam, String> paramMap;


    public void init(final HttpServletRequest req, final HttpServletResponse resp, final ExportType type) {
        try {
            user = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            if (user != null) {
                incrementCounter(user);
            }
            paramMap = new HashMap<ApiParam, String>();

            String items[] = {
                    Params.PARAM_POINT,
                    Params.PARAM_VALUE,
                    Params.PARAM_JSON,
                    Params.PARAM_NOTE,
                    Params.PARAM_LAT,
                    Params.PARAM_LNG,
                    Params.PARAM_TIMESTAMP,
                    Params.PARAM_DATA,
                    Params.PARAM_UUID,
                    Params.PARAM_FORMAT,
                    Params.PARAM_NAME,
                    Params.PARAM_POINTS,
                    Params.PARAM_COUNT,
                    Params.PARAM_AUTO_SCALE,
                    Params.PARAM_CATEGORY

            };



            for (String s : items) {
                paramMap.put(ApiParam.get(s), req.getParameter(s));
            }
            addResponseHeaders(resp, type);

        } catch (NimbitsException e) {
            user = null;
            log.severe(e.getMessage());
        }
    }
    public static void addResponseHeaders(final HttpServletResponse resp, final ExportType type) {
        if (! type.equals(ExportType.unknown)) {
            resp.setContentType(type.getCode());
        }
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }
    protected String getParam(final ApiParam param) {
        if (paramMap.containsKey(param)) {
            return paramMap.get(param);
        }
        else {
            return null;
        }
    }

    protected boolean containsParam(final ApiParam param) {

        return paramMap.containsKey(param) && !Utils.isEmptyString(paramMap.get(param));

    }
    private void incrementCounter(final User user) {
        ShardedCounter counter = getOrCreateCounter(user.getEmail());
        counter.increment();
    }

    private ShardedCounter getOrCreateCounter(final EmailAddress email) {
        CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getCounter(email.getValue());
        if (counter == null) {
            counter = factory.createCounter(email.getValue());
            counter.addShard();

        }
        return counter;
    }
}
