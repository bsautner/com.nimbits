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

package com.nimbits.server.jpa;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.EntityDescription;
import com.nimbits.client.model.server.Server;
import com.nimbits.client.model.server.ServerModelFactory;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 12/14/11
 * Time: 12:28 PM
 */
@Table(name = "ENTITY_DESCRIPTIONS", catalog = "nimbits_schema")
@Entity
public class JpaEntityDescription implements EntityDescription {

    @Column(name = "ID_ENTITY_DESCRIPTIONS", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_SERVER", nullable = false, insertable = false, updatable = false, referencedColumnName = "id_server")
    private JpaServer server;

    @Column(name = "FK_SERVER", nullable = false, insertable = true, updatable = false)
    @Basic
    private int fkServer;


    @Column(name = "ENTITY_NAME", nullable = false, insertable = true, updatable = true, length = 45, precision = 0)
    @Basic
    private String pointName;


    @Column(name = "ENTITY_TYPE", nullable = false, insertable = true, updatable = true)
    @Basic
    private int entityType;


    @Column(name = "UUID", nullable = false, insertable = true, updatable = true, length = 100, precision = 0)
    @Basic
    private String uuid;

    @Column(name = "ENTITY_DESC", nullable = true, insertable = true, updatable = true, length = 500, precision = 0)
    @Basic
    private String pointDesc;

    @Basic(optional = false)
    @Column(name = "ts", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date ts = new java.util.Date();

    public JpaEntityDescription(EntityDescription p) throws NimbitsException {
        this.server = new JpaServer(p.getServer());
        this.fkServer = this.server.getIdServer();
        this.pointName = p.getName();
        this.uuid = p.getKey();
        this.pointDesc = p.getDesc();
        this.setEntityType(p.getEntityType());
    }

    @Override
    public int getIdPoint() {
        return idPoint;
    }

    @Override
    public Server getServer() throws NimbitsException {
        return ServerModelFactory.createServer(server);
    }

    @Override
    public String getKey() {
        return uuid;
    }

    @Override
    public String getName() {
        return pointName;
    }

    @Override
    public String getDesc() {
        return pointDesc;
    }


    public Date getTs() {
        return ts;
    }

    public JpaEntityDescription(JpaServer server, String pointName, String uuid, String pointDesc) {
        this.server = server;
        this.pointName = pointName;
        this.uuid = uuid;
        this.pointDesc = pointDesc;
    }

    public JpaEntityDescription() {
    }

    public void setEntityName(String pointName) {
        this.pointName = pointName;
    }

    public void setPointDesc(String pointDesc) {
        this.pointDesc = pointDesc;
    }


    public EntityType getEntityType() {
        return EntityType.get(entityType);
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType.getCode();
    }

}
