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
import org.eclipse.om2m.commons.entities.LocationPolicyEntity;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.ConflictException;
import org.eclipse.om2m.commons.exceptions.NotImplementedException;
import org.eclipse.om2m.commons.exceptions.NotPermittedAttrException;
import org.eclipse.om2m.commons.exceptions.Om2mException;
import org.eclipse.om2m.commons.exceptions.ResourceNotFoundException;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.resource.LocationPolicy;
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
public class LocationPolicyController extends Controller {

    @Override
    public ResponsePrimitive doCreate(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);
        DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
        if (dao == null) {
            throw new ResourceNotFoundException("Cannot find parent resource");
        }
    
        ResourceEntity parentEntity = (ResourceEntity) dao.find(transaction, request.getTargetId());
        
        if (parentEntity == null) {
            throw new ResourceNotFoundException("Can't find parent resource");
        }        
        // request is create in the cse base  so parent is cse base not group entity
        
        LocationPolicy locationPolicy = null;
        Group group = null;
        try {
            if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
                locationPolicy = (LocationPolicy) request.getContent();
            } else {
                locationPolicy = (LocationPolicy) DataMapperSelector.getDataMapperList().
                                 get(request.getRequestContentType()).stringToObj((String)request.getContent());
            }
        } catch (ClassCastException e){
            throw new BadRequestException("Incorrect resource representation in content", e);
        }       
        if (locationPolicy == null){
            throw new BadRequestException("Error in provided content");
        }       
        
        LocationPolicyEntity locationPolicyEntity = new LocationPolicyEntity();

        ControllerUtil.CreateUtil.fillEntityFromAnnounceableResource(locationPolicy, locationPolicyEntity);

        String generatedId = generateId();
        locationPolicyEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.LOCATIONPOLICY + Constants.PREFIX_SEPERATOR + generatedId);
        locationPolicyEntity.setCreationTime(DateUtil.now());
        locationPolicyEntity.setLastModifiedTime(DateUtil.now());
        locationPolicyEntity.setParentID(parentEntity.getResourceID());
        locationPolicyEntity.setResourceType(ResourceType.LOCATION_POLICY);

        if (locationPolicy.getName() != null){
            if (!Patterns.checkResourceName(locationPolicy.getName())){
                throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
            }
            locationPolicyEntity.setName(locationPolicy.getName());
        } else
        if(request.getName() != null){
            if (!Patterns.checkResourceName(request.getName())){
                throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
            }
            locationPolicyEntity.setName(request.getName());
        } else {
            locationPolicyEntity.setName(ShortName.LOCATIONPOLICY + "_" + generatedId);
        }
        locationPolicyEntity.setHierarchicalURI(parentEntity.getHierarchicalURI()+ "/" + locationPolicyEntity.getName());


        if (!UriMapper.addNewUri(locationPolicyEntity.getHierarchicalURI(), locationPolicyEntity.getResourceID(), ResourceType.LOCATION_POLICY)){
            throw new ConflictException("Name already present in the parent collection.");
        }

        dbs.getDAOFactory().getLocationPolicyDAO().create(transaction, locationPolicyEntity);

        // Get the managed object from db
        LocationPolicyEntity locationPolicyDB = dbs.getDAOFactory().getLocationPolicyDAO().find(transaction, locationPolicyEntity.getResourceID());

        dao.update(transaction, parentEntity);
        transaction.commit();

        response.setResponseStatusCode(ResponseStatusCode.CREATED);
        setLocationAndCreationContent(request, response, locationPolicyDB);
        return response;


    }


    @Override
    public ResponsePrimitive doRetrieve(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);
        
        LocationPolicyEntity locationPolicyEntity = dbs.getDAOFactory().getLocationPolicyDAO().find(transaction, request.getTargetId());

        if (locationPolicyEntity == null){
            throw new ResourceNotFoundException("Resource not found");
        }   
        // TODO: check
        // checkACP(LocationPolicyEntity.getAccessControlPolicies(), request.getFrom(), 
        //        Operation.RETRIEVE);
    

        // Create the object used to create the representation of the resource TODO
        LocationPolicy location = EntityMapperFactory.getLocationPolicyMapper().mapEntityToResource(locationPolicyEntity, request);
        response.setContent(location);

        response.setResponseStatusCode(ResponseStatusCode.OK);

        return response;

	}
     
    // TODO

    @Override
    public ResponsePrimitive doUpdate(RequestPrimitive request) {

            return null;
	}

    @Override
    public ResponsePrimitive doDelete(RequestPrimitive request) {
            return null;
	}
}
