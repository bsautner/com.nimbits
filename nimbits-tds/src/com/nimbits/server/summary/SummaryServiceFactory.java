package com.nimbits.server.summary;

import com.nimbits.client.service.summary.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/16/12
 * Time: 12:38 PM
 */
public class SummaryServiceFactory {

    public static SummaryService getInstance() {

        return new SummaryServiceImpl();
    }
}
