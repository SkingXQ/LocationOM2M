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
import org.eclipse.om2m.commons.resource.LocationParameter;
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
 * Controller for Location Parameter
 *
 */
public class LocationParameterController extends Controller {

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
        
        LocationParameter locationParameter = null;
        try {
            if(request.getRequestContentType().equals(MimeMediaType.OBJ)){
                locationParameter = (LocationParameter) request.getContent();
            } else {
                locationParameter = (LocationParameter) DataMapperSelector.getDataMapperList().
                                 get(request.getRequestContentType()).stringToObj((String)request.getContent());
            }
        } catch (ClassCastException e){
            throw new BadRequestException("Incorrect resource representation in content", e);
        }       
        if (locationParameter == null){
            throw new BadRequestException("Error in provided content");
        }       
        
        LocationParameterEntity locationParameterEntity = new LocationParameterEntity();

        ControllerUtil.CreateUtil.fillEntityFromAnnounceableResource(locationParameter, locationParameterEntity);

        String generatedId = generateId();
        locationParameterEntity.setResourceID("/" + Constants.CSE_ID + "/" + ShortName.LOCATIONPARAMETER + Constants.PREFIX_SEPERATOR + generatedId);
        locationParameterEntity.setCreationTime(DateUtil.now());
        locationParameterEntity.setLastModifiedTime(DateUtil.now());
        locationParameterEntity.setParentID(parentEntity.getResourceID());
        locationParameterEntity.setResourceType(ResourceType.LOCATION_PARAMETER);
        locationParameterEntity.setLocationName(locationParameter.getLocationName());
        locationParameterEntity.setLocationTargetID(locationParameter.getLocationTargetID());
        locationParameterEntity.setLocationServer(locationParameter.getLocationServer());
        locationParameterEntity.setLocationContainerID(locationParameter.getLocationContainerID());
        locationParameterEntity.setLocationStatus(locationParameter.getLocationStatus());
        locationParameterEntity.setLocationSource(locationParameter.getLocationSource());
    
        if (locationParameter.getName() != null){
            if (!Patterns.checkResourceName(locationParameter.getName())){
                throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
            }
            locationParameterEntity.setName(locationParameter.getName());
        } else
        if(request.getName() != null){
            if (!Patterns.checkResourceName(request.getName())){
                throw new BadRequestException("Name provided is incorrect. Must be:" + Patterns.ID_STRING);
            }
            locationParameterEntity.setName(request.getName());
        } else {
            locationParameterEntity.setName(ShortName.LOCATIONPARAMETER + "_" + generatedId);
        }
        locationParameterEntity.setHierarchicalURI(parentEntity.getHierarchicalURI()+ "/" + locationParameterEntity.getName());


        if (!UriMapper.addNewUri(locationParameterEntity.getHierarchicalURI(), locationParameterEntity.getResourceID(), ResourceType.LOCATION_PARAMETER)){
            throw new ConflictException("Name already present in the parent collection.");
        }

        dbs.getDAOFactory().getLocationParameterDAO().create(transaction, locationParameterEntity);

        // Get the managed object from db
        LocationParameterEntity locationParameterDB = dbs.getDAOFactory().getLocationParameterDAO().find(transaction, locationParameterEntity.getResourceID());
        ((CSEBaseEntity) parentEntity).getChildLocationParameter().add(locationParameterDB);
        dao.update(transaction, parentEntity);
        transaction.commit();

        response.setResponseStatusCode(ResponseStatusCode.CREATED);
        setLocationAndCreationContent(request, response, locationParameterDB);
        return response;


    }


    @Override
    public ResponsePrimitive doRetrieve(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);
        
        LocationParameterEntity locationParameterEntity = dbs.getDAOFactory().getLocationParameterDAO().find(transaction, request.getTargetId());

        if (locationParameterEntity == null){
            throw new ResourceNotFoundException("Resource not found");
        }   
        // TODO: check
        // checkACP(LocationPolicyEntity.getAccessControlPolicies(), request.getFrom(), 
        //        Operation.RETRIEVE);
    

        // Create the object used to create the representation of the resource TODO
        LocationParameter locationParameter = EntityMapperFactory.getLocationParameterMapper().mapEntityToResource(locationParameterEntity, request);
        response.setContent(locationParameter);

        response.setResponseStatusCode(ResponseStatusCode.OK);

        return response;

	}
     
    // TODO

    @Override
    public ResponsePrimitive doUpdate(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);

        // retrieve the resource from database
        LocationParameterEntity locationParameterEntity = dbs.getDAOFactory().getLocationParameterDAO().find(transaction, request.getTargetId());
        if (locationParameterEntity == null) {
            throw new ResourceNotFoundException("Resource not found");
        }
        // check if content is present
        if (request.getContent() == null) {
            throw new BadRequestException("A content is requiered for Container update");
        }

        // create the java object from the resource representation
        // get the object from the representation
        LocationParameter locationParameter = null;
        try{
            if (request.getRequestContentType().equals(MimeMediaType.OBJ)){
                locationParameter = (LocationParameter) request.getContent();
            } else {
                locationParameter = (LocationParameter)DataMapperSelector.getDataMapperList()
                    .get(request.getRequestContentType()).stringToObj((String)request.getContent());
            }

        } catch (ClassCastException e){
            throw new BadRequestException("Incorrect resource representation in content", e);
        }
        if (locationParameter == null){
            throw new BadRequestException("Error in provided content");
        }

        LocationParameter modifiedAttributes = new LocationParameter();
        // locationSource        O
        if(locationParameter.getLocationSource() != null){
            locationParameterEntity.setLocationSource(locationParameter.getLocationSource());
            modifiedAttributes.setLocationSource(locationParameter.getLocationSource());
        }
        // locationSource        O
        if(locationParameter.getLocationServer() != null){
            locationParameterEntity.setLocationServer(locationParameter.getLocationServer());
            modifiedAttributes.setLocationServer(locationParameter.getLocationServer());
        }

        // locationSource        O
        if(locationParameter.getLocationName() != null){
            locationParameterEntity.setLocationName(locationParameter.getLocationName());
            modifiedAttributes.setLocationName(locationParameter.getLocationName());
        }




        locationParameterEntity.setLastModifiedTime(DateUtil.now());
        modifiedAttributes.setLastModifiedTime(locationParameterEntity.getLastModifiedTime());
        response.setContent(modifiedAttributes);
        // update the resource in the database
        dbs.getDAOFactory().getLocationParameterDAO().update(transaction, locationParameterEntity);
        transaction.commit();

        // set response status code
        response.setResponseStatusCode(ResponseStatusCode.UPDATED);
        return response;
    }

    @Override
    public ResponsePrimitive doDelete(RequestPrimitive request) {
        // Generic delete procedure
        ResponsePrimitive response = new ResponsePrimitive(request);

        // retrieve the corresponding resource from database
        LocationParameterEntity locationParameterEntity = dbs.getDAOFactory().getLocationParameterDAO().find(transaction, request.getTargetId());
        if (locationParameterEntity == null) {
            throw new ResourceNotFoundException("Resource not found");
        }

        UriMapper.deleteUri(locationParameterEntity.getHierarchicalURI());
        //Notifier.notifyDeletion(locationParameterEntity.getSubscriptions(), locationParameterEntity);

        // delete the resource in the database
        dbs.getDAOFactory().getLocationParameterDAO().delete(transaction, locationParameterEntity);
        // commit the transaction
        transaction.commit();
        // return the response
        response.setResponseStatusCode(ResponseStatusCode.DELETED);
        return response;
    }
}
