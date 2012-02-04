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

package com.nimbits.exceptions;

import com.nimbits.client.model.Const;

public class GoogleAuthenticationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;

    private String baseURL;
    private String message;

    public GoogleAuthenticationException() {
    }

    public GoogleAuthenticationException(String m) {
        message = m;

    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }


    @Override
    public String getMessage() {
        return message;
    }


}
