// Author : sking

package org.eclipse.om2m.core.controller;


import java.util.List;
import java.lang.*;

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
import org.eclipse.om2m.commons.entities.LocationParameterEntity;
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
import org.eclipse.om2m.core.util.LocationPolicyUtil;
import org.eclipse.om2m.core.util.LocationPolicyUpdateUtil;
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
        List<LocationPolicyEntity> childPol;
        DAO<ResourceEntity> dao = (DAO<ResourceEntity>) Patterns.getDAO(request.getTargetId(), dbs);
        if (dao == null) {
            throw new ResourceNotFoundException("Cannot find parent resource");
        }
        
        ResourceEntity parentEntity = (ResourceEntity) dao.find(transaction, request.getTargetId());
        childPol = ((CSEBaseEntity) parentEntity).getChildLocationPolicy();
        if (parentEntity == null) {
            throw new ResourceNotFoundException("Can't find parent resource");
        }        
        // request is create in the cse base  so parent is cse base not group entity
        LocationPolicy locationPolicy = null;
        try {
            if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
                locationPolicy = (LocationPolicy) request.getContent();
            } else {
                LOGGER.info("logging the contentType" + request.getRequestContentType());
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

        // fill entity attributes with resource attributes if exits 
        ControllerUtil.CreateUtil.fillEntityFromAnnounceableResource(locationPolicy, locationPolicyEntity);

        String generatedId = generateId();
        locationPolicyEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.LOCATIONPOLICY + Constants.PREFIX_SEPERATOR + generatedId);
        locationPolicyEntity.setCreationTime(DateUtil.now());
        locationPolicyEntity.setLastModifiedTime(DateUtil.now());
        locationPolicyEntity.setParentID(parentEntity.getResourceID());
        locationPolicyEntity.setResourceType(ResourceType.LOCATION_POLICY);
        locationPolicyEntity.setLocationName(locationPolicy.getLocationName());
        locationPolicyEntity.setLocationUpdatePeriod(locationPolicy.getLocationUpdatePeriod());
        locationPolicyEntity.setLocationSource(locationPolicy.getLocationSource());
        locationPolicyEntity.setLocationMethod(locationPolicy.getLocationMethod());
        locationPolicyEntity.setLocationGroupId(locationPolicy.getLocationGroupId());
        LocationPolicyUtil.createLocationInfo(locationPolicyEntity, locationPolicy);
        locationPolicyEntity.setLocationStatus(locationPolicy.getLocationStatus());
        locationPolicyEntity.setContainerName(locationPolicy.getContainerName());
        locationPolicyEntity.setLocationParameter(locationPolicy.getLocationParameter());

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

        // uri mapper?
        if (!UriMapper.addNewUri(locationPolicyEntity.getHierarchicalURI(), locationPolicyEntity.getResourceID(), ResourceType.LOCATION_POLICY)){
            throw new ConflictException("Name already present in the parent collection.");
        }
       
        dbs.getDAOFactory().getLocationPolicyDAO().create(transaction, locationPolicyEntity);
        
        // Get the managed object from db
        LocationPolicyEntity locationPolicyDB = dbs.getDAOFactory().getLocationPolicyDAO().find(transaction, locationPolicyEntity.getResourceID());

        childPol.add(locationPolicyDB);
        dao.update(transaction, parentEntity);
        transaction.commit();

        response.setResponseStatusCode(ResponseStatusCode.CREATED);
        // eneity to resource seting the attributes controller/Controller.java 
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
        //if (request.getResultContent().equals(10)) {
        //    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaahello");
       // }

        // Create the object used to create the representation of the resource TODO
        LocationPolicy location = EntityMapperFactory.getLocationPolicyMapper().mapEntityToResource(locationPolicyEntity, request);
        response.setContent(location);

        response.setResponseStatusCode(ResponseStatusCode.OK);
        return response;

	}
     
    // TODO

    @Override
    public ResponsePrimitive doUpdate(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);

        // retrieve the resource from database
        LocationPolicyEntity locationPolicyEntity = dbs.getDAOFactory().getLocationPolicyDAO().find(transaction, request.getTargetId());
        LocationParameterEntity locationParameterEmtity = dbs.getDAOFactory().getLocationParameterDAO().find(transaction, (Object) locationPolicyEntity.getLocationParameter());
        if (locationPolicyEntity == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        // check if content is present
        if (request.getContent() == null) {
            throw new BadRequestException("A content is requiered for Container update");
        }

        // create the java object from the resource representation
        // get the object from the representation
        LocationPolicy locationPolicy = null;
        try{
            if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
                locationPolicy = (LocationPolicy) request.getContent();
            } else {
                locationPolicy = (LocationPolicy)DataMapperSelector.getDataMapperList()
                    .get(request.getRequestContentType()).stringToObj((String)request.getContent());
            }

        } catch (ClassCastException e){
            throw new BadRequestException("Incorrect resource representation in content", e);
        }
        if (locationPolicy == null){
            throw new BadRequestException("Error in provided content");
        }

        LocationPolicy modifiedAttributes = new LocationPolicy();
        // locationGroupId        O
        if(locationPolicy.getLocationGroupId() != null){
            locationPolicyEntity.setLocationGroupId(locationPolicy.getLocationGroupId());
            modifiedAttributes.setLocationGroupId(locationPolicy.getLocationGroupId());
        }
        // locationParameter       O
        if(locationPolicy.getLocationParameter() != null){
            locationPolicyEntity.setLocationParameter(locationPolicy.getLocationParameter());
            modifiedAttributes.setLocationParameter(locationPolicy.getLocationParameter());
        }

        // locationMethod        O
        if(locationPolicy.getLocationMethod() != null){
            locationPolicyEntity.setLocationMethod(locationPolicy.getLocationMethod());
            modifiedAttributes.setLocationMethod(locationPolicy.getLocationMethod());
        }

        LocationPolicyUpdateUtil.updateLocationInfo(locationPolicy, locationPolicyEntity);

        // locationUpdatePeriod        O
        if(locationPolicy.getLocationUpdatePeriod() != null){
            locationPolicyEntity.setLocationUpdatePeriod(locationPolicy.getLocationUpdatePeriod());
            modifiedAttributes.setLocationUpdatePeriod(locationPolicy.getLocationUpdatePeriod());
        }

        locationPolicyEntity.setLastModifiedTime(DateUtil.now());
        modifiedAttributes.setLastModifiedTime(locationPolicyEntity.getLastModifiedTime());
        response.setContent(modifiedAttributes);
        // update the resource in the database
        dbs.getDAOFactory().getLocationPolicyDAO().update(transaction, locationPolicyEntity);
        transaction.commit();

        // set response status code
        response.setResponseStatusCode(ResponseStatusCode.UPDATED);
        return response;
    }

    @Override
    public ResponsePrimitive doDelete(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);

        // retrieve the corresponding resource from database
        LocationPolicyEntity locationPolicyEntity = dbs.getDAOFactory().getLocationPolicyDAO().find(transaction, request.getTargetId());
        if (locationPolicyEntity == null) {
            throw new ResourceNotFoundException("Resource not found");
        }

        UriMapper.deleteUri(locationPolicyEntity.getHierarchicalURI());
        //Notifier.notifyDeletion(locationPolicyEntity.getSubscriptions(), locationPolicyEntity);

        // delete the resource in the database
        dbs.getDAOFactory().getLocationPolicyDAO().delete(transaction, locationPolicyEntity);
        // commit the transaction
        transaction.commit();
        // return the response
        response.setResponseStatusCode(ResponseStatusCode.DELETED);
        return response;
    }
}
