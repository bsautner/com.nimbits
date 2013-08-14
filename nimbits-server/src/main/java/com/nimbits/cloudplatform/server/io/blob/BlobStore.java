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

package com.nimbits.cloudplatform.server.io.blob;

import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.model.entity.EntityName;

import java.io.IOException;

/**
 * Created by Benjamin Sautner
 * User: ubuntu
 * Date: 11/15/11
 * Time: 5:05 PM
 *
 */
public interface BlobStore {
    String createFile(EntityName name, final String data, final ExportType exportType) throws IOException;

}
