package com.nimbits.server.orm.jpa;

import javax.persistence.*;
import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 2:12 PM
 */
@Table(name = "SEARCH_LOG", schema = "", catalog = "nimbits_schema")
@Entity
public class JpaSearchLog {

    @Column(name = "ID_SEARCH_LOG", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSearchLog;

    @Column(name = "SEARCH_TEXT", nullable = false, insertable = true, updatable = true, length = 200, precision = 0)
    @Basic
    private String searchText;

    @Column(name = "SEARCH_COUNT", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private int searchCount;

    @Basic(optional = false)
    @Column(name = "TS", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date ts = new java.util.Date();

    public JpaSearchLog() {
    }

    public JpaSearchLog(String searchText) {
        this.searchText = searchText;
        this.searchCount =1;
        this.ts = new Date();

    }

    public int getIdSearchLog() {
        return idSearchLog;
    }

    public void setIdSearchLog(int idSearchLog) {
        this.idSearchLog = idSearchLog;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }


    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }



    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaSearchLog that = (JpaSearchLog) o;

        if (idSearchLog != that.idSearchLog) return false;
        if (searchCount != that.searchCount) return false;
        if (searchText != null ? !searchText.equals(that.searchText) : that.searchText != null) return false;
        if (ts != null ? !ts.equals(that.ts) : that.ts != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idSearchLog;
        result = 31 * result + (searchText != null ? searchText.hashCode() : 0);
        result = 31 * result + searchCount;
        result = 31 * result + (ts != null ? ts.hashCode() : 0);
        return result;
    }
}
