package com.nimbits.server.api;

import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
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
                    Const.Params.PARAM_POINT,
                    Const.PARAM_VALUE,
                    Const.Params.PARAM_JSON,
                    Const.Params.PARAM_NOTE,
                    Const.Params.PARAM_LAT,
                    Const.Params.PARAM_LNG,
                    Const.Params.PARAM_TIMESTAMP,
                    Const.PARAM_DATA,
                    Const.PARAM_UUID,
                    Const.Params.PARAM_FORMAT,
                    Const.Params.PARAM_NAME,
                    Const.Params.PARAM_POINTS,
                    Const.Params.PARAM_COUNT,
                    Const.Params.PARAM_AUTO_SCALE,
                    Const.Params.PARAM_CATEGORY

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
