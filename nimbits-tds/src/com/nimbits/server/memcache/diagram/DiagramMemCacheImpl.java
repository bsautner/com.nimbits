package com.nimbits.server.memcache.diagram;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.diagram.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.dao.diagram.*;
import com.nimbits.server.diagram.*;
import com.nimbits.server.pointcategory.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/3/12
 * Time: 2:43 PM
 */
public class DiagramMemCacheImpl implements DiagramTransaction {
    private User user;

    public DiagramMemCacheImpl(User u) {
        this.user = u;
    }

    @Override
    public void addDiagram(final BlobKey blobKey,final DiagramName name) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        DiagramTransactionFactory.getDaoInstance(user).addDiagram(blobKey, name);
    }

    @Override
    public List<Diagram> getDiagramsByCategory(final Category c) {
        return  DiagramTransactionFactory.getDaoInstance(user).getDiagramsByCategory(c);
    }

    @Override
    public void moveDiagram(final DiagramName diagramName, final CategoryName newCategoryName) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        DiagramTransactionFactory.getDaoInstance(user).moveDiagram(diagramName, newCategoryName);
    }

    @Override
    public void deleteDiagram(final Diagram diagram) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        DiagramTransactionFactory.getDaoInstance(user).deleteDiagram(diagram);
    }

    @Override
    public Diagram getDiagramByName(final DiagramName name) {
        return  DiagramTransactionFactory.getDaoInstance(user).getDiagramByName(name);
    }

    @Override
    public Diagram updateDiagram(final Diagram diagram) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        return DiagramTransactionFactory.getDaoInstance(user).updateDiagram(diagram);
    }

    @Override
    public Diagram getDiagramByUuid(final String uuid) {
        return  DiagramTransactionFactory.getDaoInstance(user).getDiagramByUuid(uuid);
    }

    @Override
    public Diagram updateDiagram(final BlobKey blobKey, final DiagramName diagramName, final long id) throws NimbitsException {
        CategoryTransactionFactory.getInstance(user).purgeMemCache();
        return  DiagramTransactionFactory.getDaoInstance(user).updateDiagram(blobKey, diagramName, id);
    }
}
