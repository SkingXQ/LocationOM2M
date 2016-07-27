// Author : sking

package org.eclipse.om2m.core.controller;


import java.util.List;

import org.eclipse.om2m.commons.constants.ConsistencyStrategy;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MemberType;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceStatus;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AeEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.entities.LocationEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.NotPermittedAttrException;
import org.eclipse.om2m.commons.exceptions.Om2mException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.utils.Util.DateUtil;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.entitymapper.EntityMapperFactory;
import org.eclipse.om2m.core.notifier.Notifier;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.core.util.ControllerUtil;
import org.eclipse.om2m.core.util.ControllerUtil.UpdateUtil;
import org.eclipse.om2m.core.util.GroupUtil;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;


/**
 * Controller for Location
 *
 */
public class LocationController extends Controller {

    @Override
    public ResponsePrimitive doCreate(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);
        DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
        if (dao == null) {
            throw new ResourceNotFoundException("Cannot find parent resource");
        }
        // request is create in the cse base  so parent is cse base not group entity
	}


    @Override
    public ResponsePrimitive doRetrieve(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);
        
        LocationEntity locationEntity = dbs.getDAOFactory().getLocationDAO().find(transaction, request.getTargetId());

        if (LocationEntity == null){
            throw new ResourceNotFoundException("Resource not found");
        }   
        // TODO: check
        checkACP(LocationEntity.getAccessControlPolicies(), request.getFrom(), 
                Operation.RETRIEVE);
    

        // Create the object used to create the representation of the resource TODO
        LocationPolicy location = EntityMapperFactory.getLocationMapper().mapEntityToResource(LocationEntity, request);
        response.setContent(location);

        response.setResponseStatusCode(ResponseStatusCode.OK);

        return response;

	}

    @Override
    public ResponsePrimitive doUpdate(RequestPrimitive request) {

	}

    @Override
    public ResponsePrimitive doDelete(RequestPrimitive request) {

	}
}
