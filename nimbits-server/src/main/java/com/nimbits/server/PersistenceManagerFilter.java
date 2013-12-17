/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import java.io.IOException;

public class PersistenceManagerFilter implements Filter {

    private boolean closeFlag;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String closeFlagText = filterConfig.getInitParameter("closeFlag");
        if (!StringUtils.isEmpty(closeFlagText) && closeFlagText.equals("true")) {
            closeFlag = true;

        }
    }

    public void doFilter(ServletRequest request,
                         ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
           // if (closeFlag) {
                Datastore.finishRequest();
           // }
        }
    }

    @Override
    public void destroy() {

    }
}
