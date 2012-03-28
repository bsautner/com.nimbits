package com.nimbits.server.cron;

import com.google.appengine.api.datastore.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.settings.*;
import com.nimbits.server.task.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/28/12
 * Time: 1:04 PM
 */
public class QuotaResetCron  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(QuotaResetCron.class.getName());

    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        final List<User> users = UserTransactionFactory.getInstance().getUsers();
        for (final User u : users) {
            ShardedCounter count =  getOrCreateCounter(u.getEmail());
            count.increment(count.getCount() * -1);
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
