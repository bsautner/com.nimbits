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

package com.nimbits.server.io.blob;

import com.nimbits.client.service.blob.BlobService;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/19/12
 * Time: 12:42 PM
 */
public class BlobServiceFactory {

    private BlobServiceFactory() {
    }

    private static class BlobServiceHolder {
        static final BlobService instance = new BlobServiceImpl();

        private BlobServiceHolder() {
        }
    }

    public static BlobService getInstance() {

        return BlobServiceHolder.instance;

    }
}
