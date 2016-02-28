/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.server.transaction.value.service.ValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Deprecated
public class DataTableApi extends ApiBase {

    @Autowired
    private ValueService valueService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter w = resp.getWriter();
        initRequest(req, resp);

        User user = (User) req.getAttribute(Parameters.user.getText());
        String startParam = req.getParameter(Parameters.start.getText());
        String endParam = req.getParameter(Parameters.end.getText());
        String countParam = req.getParameter(Parameters.count.getText());
        String maskParam = req.getParameter(Parameters.mask.getText());

        Optional<String> mask = StringUtils.isEmpty(maskParam) ? Optional.<String>absent() : Optional.<String>of(maskParam);

        Optional<Integer> count = StringUtils.isNotEmpty(countParam) ? Optional.of(Integer.valueOf(countParam)) : Optional.<Integer>absent();
        Entity entity = getEntity(req, resp, user);
        Optional<Range<Date>> timespan;


        if (! StringUtils.isEmpty(startParam) && ! StringUtils.isEmpty(endParam) ) {
            Date start = new Date(Long.valueOf(startParam));
            Date end = new Date(Long.valueOf(endParam));
            timespan = Optional.of(Range.closed(start, end));

        }
        else {
            timespan = Optional.absent();
        }


        String chartData = valueService.getChartTable(entityDao, blobStore, user, entity, timespan, count, mask);
        w.println(chartData);

    }
}
