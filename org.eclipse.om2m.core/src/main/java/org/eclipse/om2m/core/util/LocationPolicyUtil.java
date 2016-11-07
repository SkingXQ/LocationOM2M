/*******************************************************************************
 * Copyright (c) 2013-2016 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 *     Thierry Monteil : Project manager, technical co-manager
 *     Mahdi Ben Alaya : Technical co-manager
 *     Samir Medjiah : Technical co-manager
 *     Khalil Drira : Strategy expert
 *     Guillaume Garzone : Developer
 *     François Aïssaoui : Developer
 *
 * New contributors :
 *******************************************************************************/
package org.eclipse.om2m.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.ConsistencyStrategy;
import org.eclipse.om2m.commons.constants.MemberType;
import org.eclipse.om2m.commons.constants.AccessControl;
import org.eclipse.om2m.commons.constants.CSEType;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.LocationPolicyEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.LocationParameterEntity;
import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.resource.LocationPolicy;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.MemberNonFoundException;
import org.eclipse.om2m.commons.exceptions.MemberTypeInconsistentException;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;
import org.eclipse.om2m.core.comm.RestClient;


public class LocationPolicyUtil {

    private static final Log LOGGER = LogFactory.getLog(LocationPolicyUtil.class);

    private LocationPolicyUtil(){}


    public static void createLocationInfo(LocationPolicyEntity locationPolicyEntity, LocationPolicy locationPolicy) 
        throws MemberNonFoundException, MemberTypeInconsistentException{
        if (locationPolicy.getLocationGroupId().equals("local")) {
            ResponsePrimitive response = createLocalContainer("localCnt");
            String containerName = findMatch(riPattern, (String) response.getContent());
            locationPolicy.setContainerName(containerName);
            String content = ((String) response.getContent());
            String ri = findMatch(riPattern, content);
            response = retrieveLocalLocationParameter(locationPolicy);
            String server = findMatch(serverPattern, (String )response.getContent());
            //response = createLocalData(ri, getLocation(server));
            response = createLocalData(ri, locationPolicy.getContainerName());
        } else {
            ResponsePrimitive response = retrieveLocalGroup(locationPolicy);
            String macp = findMatch(macpPattern, (String) response.getContent());
            String[] macps = macp.split(" ");
            String parameterContent = getParameterContent(locationPolicy);
            String locationInfo = "";
            for(int i=0; i< macps.length; i++) {
                ResponsePrimitive r = retrieveLocalRemoteCSE(macps[i]); 
                String remoteUrl = findMatch(poaPattern, (String) r.getContent()) + "~" + 
                                   findMatch(csiPattern, (String) r.getContent());
                String[] t = macps[i].split("/");
                r = createRemoteParameter(t[(t.length-1)] , remoteUrl, parameterContent);
                t = (findMatch(riPattern, (String) r.getContent())).split("/");
                LocationPolicy l = locationPolicy;
                l.setLocationGroupId("local");
                l.setLocationParameter(findMatch(riPattern, (String) r.getContent()));
                String policyContent = DataMapperSelector.getDataMapperList().get("application/xml").objToString(l);
                r = createRemotePolicy(t[(t.length-1)], remoteUrl, policyContent);
                String[] containerName = (findMatch(locationContainerNamePattern, (String) r.getContent())).split("/");
                String ru = remoteUrl + "/" + containerName[containerName.length-1];
                r = retrieveRemoteContainerOrData(ru);
                containerName  = (findMatch(chPattern, (String) r.getContent())).split("/");
                ru = remoteUrl + "/" + containerName[containerName.length-1];
                r = retrieveRemoteContainerOrData(ru);
                locationInfo = locationInfo  + findMatch(conPattern, (String) r.getContent());
            }
            response = createLocalContainer("localCnt");
            locationPolicy.setContainerName(findMatch(piPattern, (String) response.getContent()));
            String ri = findMatch(riPattern, ((String) response.getContent()));
            response = createLocalData(ri, locationInfo);
        }
    }

    public static ResponsePrimitive retrieveRemoteContainerOrData(String remotePoa) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.RETRIEVE);
        request.setTo(remotePoa);
        request.setRequestContentType(MimeMediaType.XML);
        request.setResultContent(BigInteger.valueOf(5));
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.OK)) {
            LOGGER.info("Retrieval the informathion of parameter");
        } else {
            LOGGER.info("Error on retrievaling the information of location parameter");
        }
        return response;
    }

    public static ResponsePrimitive createRemotePolicy(String name, String remotePoa, String content) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.CREATE);
        request.setTo(remotePoa);
        request.setResourceType(ResourceType.LOCATION_POLICY);
        request.setRequestContentType(MimeMediaType.XML);
        request.setName(name);
        request.setContent(content);
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
            || response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
            LOGGER.info("create remote parameter");
        } else {
            LOGGER.info("Error in registration to another CSE. Retrying in 10s");
        }
        return response;
    }

    public static ResponsePrimitive createRemoteParameter(String name, String remotePoa, String content) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.CREATE);
        request.setTo(remotePoa);
        request.setResourceType(ResourceType.LOCATION_PARAMETER);
        request.setRequestContentType(MimeMediaType.XML);
        request.setName(name);
        request.setContent(content);
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
            || response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
            LOGGER.info("create remote parameter");
        } else {
            LOGGER.info("Error in registration to another CSE. Retrying in 10s");
        }
        return response;
    }

    public static ResponsePrimitive createLocalContainer(String name) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.CREATE);
        String remotePoa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~/" + Constants.CSE_ID;
        request.setTo(remotePoa);
        request.setResourceType(ResourceType.CONTAINER);
        request.setRequestContentType(MimeMediaType.XML); 
        request.setName(name);
        request.setContent(containerString);
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
            || response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
            LOGGER.info("create local container");
        } else {
            LOGGER.info("Error in registration to another CSE. Retrying in 10s");
        }
        return response;
    }

    public static ResponsePrimitive createLocalData(String parent, String message) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.CREATE);
        String remotePoa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~" + parent;
        request.setTo(remotePoa);
        request.setResourceType(ResourceType.CONTENT_INSTANCE);
        request.setRequestContentType(MimeMediaType.XML);
        String content = dataString.replace(replaceMessage, message);
        request.setContent(content);
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
            || response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
            LOGGER.info("create local container");
        } else {
            LOGGER.info("Error in creating local container");
        }
        return response;
    }

    public static ResponsePrimitive retrieveLocalLocationParameter(LocationPolicy locationPolicy) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.RETRIEVE);
        String remotePoa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~"+ locationPolicy.getLocationParameter();
        request.setTo(remotePoa);
        request.setRequestContentType(MimeMediaType.XML);
        request.setResultContent(BigInteger.valueOf(5));
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.OK)) {
            LOGGER.info("Retrieval the informathion of parameter");
        } else {
            LOGGER.info("Error on retrievaling the information of location parameter");
        }
        return response;    
    }

    public static ResponsePrimitive retrieveLocalGroup(LocationPolicy locationPolicy) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.RETRIEVE);
        String remotePoa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~"+ locationPolicy.getLocationGroupId();
        request.setTo(remotePoa);
        request.setRequestContentType(MimeMediaType.XML);
        request.setResultContent(BigInteger.valueOf(5));
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.OK)) {
            LOGGER.info("Retrieval the informathion of parameter");
        } else {
            LOGGER.info("Error on retrievaling the information of location parameter");
        }
        return response;
    }

    public static ResponsePrimitive retrieveLocalRemoteCSE(String remoteCSE) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.RETRIEVE);
        String remotePoa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~"+ remoteCSE;
        request.setTo(remotePoa);
        request.setRequestContentType(MimeMediaType.XML);
        request.setResultContent(BigInteger.valueOf(5));
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.OK)) {
            LOGGER.info("Retrieval the informathion of parameter");
        } else {
            LOGGER.info("Error on retrievaling the information of location parameter");
        }
        return response;
    }

    public static String findMatch(String patternStr, String content) { 
        Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if(matcher.find())
            return matcher.group(1);
        return "";
    }

    private static String getParameterContent(LocationPolicy locationPolicy) {
        ResponsePrimitive response = retrieveLocalLocationParameter(locationPolicy);
        String content = (String)response.getContent();
        String res = locationParameterString;
        res = res.replace("TARGET", findMatch(locationTargetPattern, content));
        res = res.replace("SERVER", findMatch(locationServerPattern, content));
        res = res.replace("CONTAINER", findMatch(locationContainerPattern, content));
        res = res.replace("NAME", findMatch(locationNamePattern, content));
        res = res.replace("STATUS", findMatch(locationStatusPattern, content));
        return res;

    }

    private static String getLocation(String server) {

        String res = server;
        return res;
    }
 

  

     private static String dataString = "<om2m:cin xmlns:om2m=\"http://www.onem2m.org/xml/protocols\">\n<cnf>message</cnf>\n<con>\nHELLO\n</con>\n</om2m:cin>";

     private static String locationParameterString = "<m2m:locationParameter xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"><locationTargetID>TARGET</locationTargetID><locationServer>SERVER</locationServer><locationContainerID>CONTAINER</locationContainerID><locationame>NAME</locationame><locationStatus>STATUS</locationStatus></m2m:locationParameter>";

     private static String containerString = "<om2m:cnt xmlns:om2m=\"http://www.onem2m.org/xml/protocols\">\n</om2m:cnt>";

     private static String replaceMessage = "HELLO";

     // used for withdraw information from 
     private static String riPattern = "<ri>(.*)</ri>";
     private static String piPattern = "<pi>(.*)</pi>";
     private static String serverPattern = "<locationServer>(.*)</locationServer>";
     private static String containerPattern = "<locationcontainername>(.*)</locationcontainername>";
     private static String macpPattern = "<macp>(.*)</macp>";
     private static String poaPattern = "<poa>(.*)</poa>";
     private static String csiPattern = "<csi>(.*)</csi>";
     private static String locationTargetPattern = "<locationTargetID>(.*)</locationTargetID>";
     private static String locationServerPattern = "<locationServer>(.*)</locationServer>";
     private static String locationContainerPattern = "<locationContainerID>(.*)</locationContainerID>";
     private static String locationNamePattern = "<locationame>(.*)</locationame>";
     private static String locationStatusPattern = "<locationStatus>(.*)</locationStatus>";
     private static String locationContainerNamePattern = "<locationcontainername>(.*)</locationcontainername>";
     private static String chPattern = "<ch.*>(.*)</ch>";
     private static String conPattern = "<con>(.*)</con>";
}
