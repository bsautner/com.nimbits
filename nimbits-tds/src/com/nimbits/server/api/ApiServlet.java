package com.nimbits.server.api;

import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.setting.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.settings.*;
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
    private Map<Parameters, String> paramMap;


    public void init(final HttpServletRequest req, final HttpServletResponse resp, final ExportType type) throws NimbitsException {

            user = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            if (user != null) {
                incrementCounter(user);
            }
            paramMap = new HashMap<Parameters, String>();

            Parameters items[] = {
                    Parameters.point,
                    Parameters.value,
                    Parameters.json,
                    Parameters.note,
                    Parameters.lat,
                    Parameters.lng,
                    Parameters.timestamp,
                    Parameters.data,
                    Parameters.uuid,
                    Parameters.format,
                    Parameters.name,
                    Parameters.points,
                    Parameters.count,
                    Parameters.autoscale,
                    Parameters.category

            };



            for (Parameters s : items) {
                paramMap.put(s, req.getParameter(s.getText()));
            }
            addResponseHeaders(resp, type);


    }
    public static void addResponseHeaders(final HttpServletResponse resp, final ExportType type) {
        if (! type.equals(ExportType.unknown)) {
            resp.setContentType(type.getCode());
        }
        resp.addHeader("Cache-Control", "no-cache");
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }
    protected String getParam(final Parameters param) {
        if (paramMap.containsKey(param)) {
            return paramMap.get(param);
        }
        else {
            return null;
        }
    }

    protected boolean containsParam(final Parameters param) {

        return paramMap.containsKey(param) && !Utils.isEmptyString(paramMap.get(param));

    }
    private void incrementCounter(final User user) throws NimbitsException {
        ShardedCounter counter = getOrCreateCounter(user.getEmail());
        counter.increment();
        if (counter.getCount() > Const.MAX_DAILY_QUOTA) {
            if (SettingsServiceFactory.getInstance().getBooleanSetting(SettingType.quotaEnabled)) {
                throw new NimbitsException(UserMessages.ERROR_QUOTA_EXCEEDED);
                //todo here is where we charge em;
            }

        }
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
