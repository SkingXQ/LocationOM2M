// Author = sking

package org.eclipse.om2m.persistence.eclipselink.internal.dao;

import java.util.List;

import org.eclipse.om2m.commons.entities.AeAnncEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.LabelEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.RemoteCseAnncEntity;
import org.eclipse.om2m.commons.entities.LocationPolicyEntity;
import org.eclipse.om2m.persistence.eclipselink.internal.DBTransactionJPAImpl;
import org.eclipse.om2m.persistence.service.DBTransaction;


public class LocationPolicyDAO extends AbstractDAO<LocationPolicyEntity>{

    @Override
    public LocationPolicyEntity find(DBTransaction dbTransaction, Object id) {
        DBTransactionJPAImpl transaction = (DBTransactionJPAImpl) dbTransaction;
        return transaction.getEm().find(LocationPolicyEntity.class, id);
    }

    @Override
    public void delete(DBTransaction dbTransaction, LocationPolicyEntity resource) {
        DBTransactionJPAImpl transaction = (DBTransactionJPAImpl) dbTransaction;
        transaction.getEm().remove(resource);
        transaction.getEm().getEntityManagerFactory().getCache().evict(CSEBaseEntity.class);
    }

    @Override
    public void update(DBTransaction dbTransaction, LocationPolicyEntity resource) {
        List<LabelEntity> lbls = processLabels(dbTransaction, resource.getLabelsEntities());
        resource.setLabelsEntities(lbls);
        super.update(dbTransaction, resource);
    }   

}


