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

package com.nimbits.server.diagram;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.DiagramNotFoundException;
import com.nimbits.client.exceptions.ObjectProtectionException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.diagram.DiagramService;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.server.user.UserTransactionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 5:33 PM
 */
public class DiagramServiceImpl extends RemoteServiceServlet implements
        RequestCallback, DiagramService {
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public String getBlobStoreUrl(String url) {
        return blobstoreService.createUploadUrl(url);
    }

    @Override
    public void onResponseReceived(Request request, Response response) {

    }

    @Override
    public void onError(Request request, Throwable throwable) {

    }

    @Override
    public void moveDiagram(final EntityName diagramName, final EntityName newEntityName) throws NimbitsException {
        User u = UserServiceFactory.getServerInstance().getHttpRequestUser(this.getThreadLocalRequest());
        DiagramTransactionFactory.getInstance(u).moveDiagram(diagramName, newEntityName);

    }

    @Override
    public void deleteDiagram(Diagram diagram) throws NimbitsException {
        User u = UserServiceFactory.getServerInstance().getHttpRequestUser(this.getThreadLocalRequest());
        DiagramTransactionFactory.getInstance(u).deleteDiagram(diagram);
    }

    @Override
    public Map<EntityName, Diagram> getDiagramsByName(final long diagramOwnerId, final Set<EntityName> names) throws NimbitsException {

        User loggedInUser = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        User diagramOwner = UserTransactionFactory.getInstance().getNimbitsUserByID(diagramOwnerId);
        Map<EntityName, Diagram> retObj = new HashMap<EntityName, Diagram>();
        for (EntityName name : names) {

            Diagram d = DiagramTransactionFactory.getInstance(diagramOwner).getDiagramByName(name);
            if (d != null && checkDiagramProtection(loggedInUser, diagramOwner, d)) {
                retObj.put(name, d);
            }
        }
        return retObj;

    }

    @Override
    public boolean checkDiagramProtection(User loggedInUser, User diagramOwner, Diagram d) {
        long loggedInUserId = 0;
        if (loggedInUser != null) {
            loggedInUserId = loggedInUser.getId();
        }
        return d != null && (loggedInUserId == diagramOwner.getId() || d.getProtectionLevel() >= 2 || d.getProtectionLevel() == 1 && diagramOwner.getConnections().contains(loggedInUserId));
    }

    @Override
    public List<Diagram> getDiagramsByCategory(User u, Category c) {
       return DiagramTransactionFactory.getInstance(u).getDiagramsByCategory(c);
    }


    @Override
    public Diagram updateDiagram(Diagram diagram) throws NimbitsException {
        User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        return DiagramTransactionFactory.getInstance(u).updateDiagram(diagram);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Diagram getDiagramByUuid(String uuid) throws ObjectProtectionException, DiagramNotFoundException, NimbitsException {
        User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        Diagram d = DiagramTransactionFactory.getInstance(u).getDiagramByUuid(uuid);

        if (d != null) {
            User diagramOwner = UserTransactionFactory.getInstance().getNimbitsUserByID(d.getUserFk());
            if (checkDiagramProtection(u, diagramOwner, d)) {
                return d;
            } else {
                throw new ObjectProtectionException(Const.MESSAGE_DIAGRAM_PROTECTION_EXCEPTION);
            }
        } else {
            throw new DiagramNotFoundException(Const.MESSAGE_DIAGRAM_NOT_FOUND_EXCEPTION);
        }


    }
}
