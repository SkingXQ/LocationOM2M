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


public class GroupLocationUtil {

    private static final Log LOGGER = LogFactory.getLog(LocationPolicyUtil.class);

    private GroupLocationUtil(){}

    public static ResponsePrimitive retrieveLocalInfo(String remotePoa) {
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

    public static List<String> retrieveRemoteCse(String name, String content) {
        Pattern pattern = Pattern.compile(chNamePattern);
        Matcher matcher = pattern.matcher(content);
        List<String> res = new ArrayList<String>();
        while(matcher.find()){
            if(!matcher.group(1).equals(name))
                res.add(matcher.group(2));
        }
        return res;
    }

    public static String createGroup(List<String> members, String poa) {
        String gs = groupString;
        gs = gs.replace("NUM", String.valueOf(members.size()));
        String mid = members.get(0);
        String macp = members.get(0);
        for(int i=1; i<members.size(); i++) {
            mid = mid + " " + members.get(i);
            macp = macp + " " + members.get(i);
        }
        gs = gs.replace("MID", mid);
        gs = gs.replace("MACP", macp);
        ResponsePrimitive response = create(ResourceType.GROUP, poa, gs);
        return findMatch(riPattern, (String) response.getContent());
    }

    public static String createLocationParameter(String locationTarget, String locationServer, 
        String locationName,int status, String poa) {
        String lp = locationParameterString;
        lp = lp.replace("TARGET", locationTarget);
        lp = lp.replace("SERVER", locationServer);
        lp = lp.replace("NAME", locationName);
        lp = lp.replace("STATUS", String.valueOf(status));
        ResponsePrimitive response = create(ResourceType.LOCATION_PARAMETER, poa, lp);
        return findMatch(riPattern, (String) response.getContent());
    }

    public static String createLocationPolicy(int locationSource, int locationUpdate, int locationStatus,
        String locationName, String locationMethod, String locationGroup,
        String locationParameter, String poa) {
        String lp = locationPolicyString;
        lp = lp.replace("LOCATIONSOURCE", String.valueOf(locationSource));
        lp = lp.replace("LOCATIONUPDATE", String.valueOf(locationUpdate));
        lp = lp.replace("LOCATIONNAME", locationName);
        lp = lp.replace("LOCATIONSTATUS", String.valueOf(locationStatus));
        lp = lp.replace("LOCATIONMETHOD", locationMethod);
        lp = lp.replace("LOCATIONGROUP", locationGroup);
        lp = lp.replace("LOCATIONPARAMETER", locationParameter);
        ResponsePrimitive response = create(ResourceType.LOCATION_POLICY, poa, lp);
        String st = findMatch(locationStatusPattern, (String) response.getContent());
        if(Integer.parseInt(st) != 1) return "None";
        return findMatch(containerPattern, (String) response.getContent());
    }

    private static ResponsePrimitive create(int type, String poa, String content) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.CREATE);
        request.setTo(poa);
        request.setResourceType(type);
        request.setRequestContentType(MimeMediaType.XML);
        request.setContent(content);
        ResponsePrimitive response = RestClient.sendRequest(request);
        if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
            || response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
            LOGGER.info("create source " + String.valueOf(type) + " success");
        } else {
            LOGGER.info("create source " + String.valueOf(type) + " fail");
        }
        return response;
    }
    
    public static ResponsePrimitive retrieve(String poa) {
        RequestPrimitive request = new RequestPrimitive();
        request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
        request.setOperation(Operation.RETRIEVE);
        request.setTo(poa);
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

     private static String locationParameterString = "<m2m:locationParameter xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"><locationTargetID>TARGET</locationTargetID><locationServer>SERVER</locationServer><locationContainerID>CONTAINER</locationContainerID><locationame>NAME</locationame><locationStatus>STATUS</locationStatus></m2m:locationParameter>";

     private static String groupString = "<m2m:grp xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"><mnm>NUM</mnm><mid>MID</mid><macp>MACP</macp></m2m:grp>";

     private static String locationPolicyString = "<m2m:locationPolicy xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"><locationsource>LOCATIONSOURCE</locationsource><locationupdateperiod>LOCATIONUPDATE</locationupdateperiod><locationame>LOCATIONNAME</locationame><locationstatus>LOCATIONSTATUS</locationstatus><locationmethod>LOCATIONMETHOD</locationmethod><locationGroupId>LOCATIONGROUP</locationGroupId><locationParameter>LOCATIONPARAMETER</locationParameter></m2m:locationPolicy>";

     // used for withdraw information from 
     private static String riPattern = "<ri>(.*)</ri>";
     private static String piPattern = "<pi>(.*)</pi>";
     private static String serverPattern = "<locationServer>(.*)</locationServer>";
     private static String containerPattern = "<locationcontainername>(.*)</locationcontainername>";
     private static String macpPattern = "<macp>(.*)</macp>";
     private static String locationTargetPattern = "<locationTargetID>(.*)</locationTargetID>";
     private static String locationServerPattern = "<locationServer>(.*)</locationServer>";
     private static String locationContainerPattern = "<locationContainerID>(.*)</locationContainerID>";
     private static String locationNamePattern = "<locationame>(.*)</locationame>";
     private static String locationStatusPattern = "<locationstatus>(.*)</locationstatus>";
     private static String locationContainerNamePattern = "<locationcontainername>(.*)</locationcontainername>";
     private static String chPattern = "<ch.*>(.*)</ch>";
     private static String chNamePattern = "<ch rn=\"(.*)\" ty=\"16\">(.*)</ch>";
     public static String conPattern = "<con>(.*)</con>";
     public static String poaPattern = "<poa>(.*)</poa>";
     public static String chCsePattern = "<ch ty=\"16\">(.*)</ch>";
     public static String csiPattern = "<csi>(.*)</csi>";
     public static String ch4Pattern = "<ch.*ty=\"4\">(.*)</ch>";
}
