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
import org.eclipse.om2m.commons.entities.LocationEntity;
import org.eclipse.om2m.persistence.eclipselink.internal.DBTransactionJPAImpl;
import org.eclipse.om2m.persistence.service.DBTransaction;


public class LocationEntityDAO extends AbstractDAO<LocationEntity>{

        @Override
        public LocationEntity find(DBTransaction dbTransaction, Object id) {
                DBTransactionJPAImpl transaction = (DBTransactionJPAImpl) dbTransaction;
                return transaction.getEm().find(LocationEntity.class, id);
        }

        @Override
        public void delete(DBTransaction dbTransaction, LocationEntity resource) {
                DBTransactionJPAImpl transaction = (DBTransactionJPAImpl) dbTransaction;
                transaction.getEm().remove(resource);
                transaction.getEm().getEntityManagerFactory().getCache().evict(CSEBaseEntity.class);
                //transaction.getEm().getEntityManagerFactory().getCache().evict(AeEntity.class);
                //transaction.getEm().getEntityManagerFactory().getCache().evict(RemoteCSEEntity.class);
                //transaction.getEm().getEntityManagerFactory().getCache().evict(AeAnncEntity.class);
                //transaction.getEm().getEntityManagerFactory().getCache().evict(RemoteCseAnncEntity.class);
        }

        @Override
        public void update(DBTransaction dbTransaction, LocationEntity resource) {
                List<LabelEntity> lbls = processLabels(dbTransaction, resource.getLabelsEntities());
                resource.setLabelsEntities(lbls);
                super.update(dbTransaction, resource);
        }

}


