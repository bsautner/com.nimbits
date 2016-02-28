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

package com.nimbits.client.model.setting;

import com.nimbits.client.enums.ServerSetting;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 5/5/11
 * Time: 3:41 PM
 */
public interface Setting extends Serializable {

    ServerSetting getSetting();

    String getValue();

    void setValue(String newValue);
}
