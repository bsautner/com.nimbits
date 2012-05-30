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

package com.nimbits.server.transactions.dao.search;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.orm.JpaSearchLog;

import java.util.List;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:22 PM
 */
public interface SearchLogTransactions {
    void addUpdateSearchLog(String searchText) throws NimbitsException;

    JpaSearchLog addSearchLog(String searchText);

    void deleteSearchLog(String searchText);

    JpaSearchLog updateSearchLog(String searchText) throws NimbitsException;

    List<JpaSearchLog> readSearchLog(String searchText);
}
