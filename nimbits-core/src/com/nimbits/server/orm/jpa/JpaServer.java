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

package com.nimbits.server.orm.jpa;

import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.server.Server;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:28 PM
 */
@Table(name = "SERVERS", catalog = "nimbits_schema")
@Entity
public class JpaServer implements Server {

    @Column(name = "ID_SERVER", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idServer;

    @Column(name = "BASE_URL", nullable = false, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    private String baseUrl;

    @Column(name = "OWNER_EMAIL", nullable = true, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    private String ownerEmail;

    @Column(name = "SERVER_VERSION", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Basic
    private String serverVersion;

    @Basic(optional = false)
    @Column(name = "ts", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ts = new Date();


    @OneToMany(mappedBy = "server", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<JpaEntityDescription> points;

    public Set<JpaEntityDescription> getPoints() {
        return points;
    }


    protected JpaServer() {
    }

//    public JpaServer(String baseUrl, String ownerEmail, String serverVersion) {
//        this.baseUrl = baseUrl;
//        this.ownerEmail = ownerEmail;
//        this.serverVersion = serverVersion;
//
//    }

    public JpaServer(Server server) {
        this.baseUrl = server.getBaseUrl();
        this.ownerEmail = server.getOwnerEmail().getValue();
        this.serverVersion = server.getServerVersion();
        this.idServer = server.getIdServer();
    }

    @Override
    public int getIdServer() {
        return idServer;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public EmailAddress getOwnerEmail() {

        return CommonFactoryLocator.getInstance().createEmailAddress(ownerEmail);
    }

    @Override
    public String getServerVersion() {
        return serverVersion;
    }

    @Override
    public Date getTs() {
        return ts;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setOwnerEmail(EmailAddress ownerEmail) {
        this.ownerEmail = ownerEmail.getValue();
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JpaServer servers = (JpaServer) o;

        if (idServer != servers.idServer) return false;
        if (baseUrl != null ? !baseUrl.equals(servers.baseUrl) : servers.baseUrl != null) return false;
        if (ownerEmail != null ? !ownerEmail.equals(servers.ownerEmail) : servers.ownerEmail != null) return false;
        if (serverVersion != null ? !serverVersion.equals(servers.serverVersion) : servers.serverVersion != null)
            return false;
        if (ts != null ? !ts.equals(servers.ts) : servers.ts != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idServer;
        result = 31 * result + (baseUrl != null ? baseUrl.hashCode() : 0);
        result = 31 * result + (ownerEmail != null ? ownerEmail.hashCode() : 0);
        result = 31 * result + (serverVersion != null ? serverVersion.hashCode() : 0);
        result = 31 * result + (ts != null ? ts.hashCode() : 0);
        return result;
    }
}
