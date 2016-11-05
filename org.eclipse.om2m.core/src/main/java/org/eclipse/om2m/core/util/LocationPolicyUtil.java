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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.ConsistencyStrategy;
import org.eclipse.om2m.commons.constants.MemberType;
import org.eclipse.om2m.commons.entities.LocationPolicyEntity;
import org.eclipse.om2m.commons.entities.ResourceEntity;
import org.eclipse.om2m.commons.entities.LocationParameterEntity;
import org.eclipse.om2m.commons.resource.LocationPolicy;
import org.eclipse.om2m.commons.exceptions.BadRequestException;
import org.eclipse.om2m.commons.exceptions.MemberNonFoundException;
import org.eclipse.om2m.commons.exceptions.MemberTypeInconsistentException;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Patterns;
import org.eclipse.om2m.core.urimapper.UriMapper;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;
import org.eclipse.om2m.core.comm.RestClient;

import org.eclipse.om2m.commons.constants.AccessControl;
import org.eclipse.om2m.commons.constants.CSEType;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.commons.constants.MimeMediaType;
import org.eclipse.om2m.commons.constants.Operation;
import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResponseStatusCode;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlOriginatorEntity;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.AccessControlRuleEntity;
import org.eclipse.om2m.commons.entities.CSEBaseEntity;
import org.eclipse.om2m.commons.entities.RemoteCSEEntity;
import org.eclipse.om2m.commons.resource.AccessControlPolicy;
import org.eclipse.om2m.commons.resource.RemoteCSE;
import org.eclipse.om2m.commons.resource.Container;
import org.eclipse.om2m.commons.resource.RequestPrimitive;
import org.eclipse.om2m.commons.resource.ResponsePrimitive;


public class LocationPolicyUtil {

	private static final Log LOGGER = LogFactory.getLog(LocationPolicyUtil.class);

	private LocationPolicyUtil(){}

        private static String localContent = "<om2m:cnt xmlns:om2m=\"http://www.onem2m.org/xml/protocols\">\n</om2m:cnt>";


	public static void createLocationInfo(LocationPolicyEntity locationPolicyEntity, LocationPolicy locationPolicy) 
			throws MemberNonFoundException, MemberTypeInconsistentException{
        	System.out.println("test create");
                if (locationPolicy.getLocationGroupId().equals("local")) {
                    System.out.println("local");
                    ResponsePrimitive response = createLocalContainer("localCnt");
                    LOGGER.info("test + " + response.getContent());
                    String content = ((String) response.getContent());
                    String ri = findMatch(riPattern, content);
                    response = createData(ri, "hello world");
                          
                } else {
                    System.out.println("remote");
                }
	}


       public static void testRestClient() {
                RequestPrimitive request = new RequestPrimitive();
                request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
                request.setOperation(Operation.RETRIEVE);
                String rpa = "http://60.205.170.115:8080/~/in-cse";
                request.setTo(rpa);
                request.setResultContent(ResultContent.ATTRIBUTES_AND_CHILD_REF);
                ResponsePrimitive response = RestClient.sendRequest(request);
                System.out.println("test + " +  response);

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
                    request.setContent(localContent);
                    ResponsePrimitive response = RestClient.sendRequest(request);
                    if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
                        || response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
                        LOGGER.info("create local container");
                    } else {
                        LOGGER.info("Error in registration to another CSE. Retrying in 10s");
                    }
                    return response;
       }

       public static ResponsePrimitive createData(String parent, String message) {
                    RequestPrimitive request = new RequestPrimitive();
                    request.setFrom(Constants.ADMIN_REQUESTING_ENTITY);
                    request.setOperation(Operation.CREATE);
                    String remotePoa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~" + parent;
                    request.setTo(remotePoa);
                    request.setResourceType(ResourceType.CONTENT_INSTANCE);
                    request.setRequestContentType(MimeMediaType.XML);
                    String content = dataString.replaceAll("hello", message);
                    request.setContent(dataString);
                    ResponsePrimitive response = RestClient.sendRequest(request);
                    if(response.getResponseStatusCode().equals(ResponseStatusCode.CREATED)
                        || response.getResponseStatusCode().equals(ResponseStatusCode.CONFLICT)){
                        LOGGER.info("create local container");
                    } else {
                        LOGGER.info("Error in registration to another CSE. Retrying in 10s");
                    }
                    return response;
       }


       public static String findMatch(String patternStr, String content) {
                    Pattern pattern = Pattern.compile(patternStr);
                    Matcher matcher = pattern.matcher(content);
                    if(matcher.find())
                        return matcher.group(1);
                    return "";
       }
      private static String dataString = "<om2m:cin xmlns:om2m=\"http://www.onem2m.org/xml/protocols\">\n<cnf>message</cnf>\n<con>\nhello\n</con>\n</om2m:cin>";

      private static String  riPattern = "<ri>(.*)</ri>";
}
