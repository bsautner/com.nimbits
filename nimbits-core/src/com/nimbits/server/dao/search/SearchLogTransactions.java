package com.nimbits.server.dao.search;

import com.nimbits.client.exception.*;
import com.nimbits.server.orm.jpa.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:22 PM
 */
public interface SearchLogTransactions {
    void addUpdateSearchLog(String searchText) throws NimbitsException;

    JpaSearchLog addSearchLog(String searchText);

    JpaSearchLog updateSearchLog(String searchText) throws NimbitsException;

    JpaSearchLog readSearchLog(String searchText);
}
