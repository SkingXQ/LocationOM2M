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
import org.eclipse.om2m.core.util.GroupLocationUtil;
import org.eclipse.om2m.persistence.service.DAO;
import org.eclipse.om2m.persistence.service.DBService;
import org.eclipse.om2m.persistence.service.DBTransaction;
import org.eclipse.om2m.core.comm.RestClient;


/**
 * Controller for Location
 * TODO: checking method
 */
public class GroupLocationController extends Controller {

    @Override
    public ResponsePrimitive doCreate(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);
        // test for content
       // String a = "test";
        //response.setContent((Object) a);
        String co = (String) request.getContent();
        ResponsePrimitive r = GroupLocationUtil.retrieveLocalInfo("http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~/" + Constants.CSE_ID);
        List<String> mncse = GroupLocationUtil.retrieveRemoteCse("", (String) r.getContent());
        String content = "<incse>" + Constants.CSE_ID + "</incse>";
        for(String m: mncse) {
            content += ("<chcse>" + getGroupLocation(m) + "</chcse>");
            
        }
        response.setContent((Object) content);
        response.setResponseStatusCode(ResponseStatusCode.OK);       
        return response;
    }


    @Override
    public ResponsePrimitive doRetrieve(RequestPrimitive request) {
        ResponsePrimitive response = new ResponsePrimitive(request);
        // test for content
       // String a = "test";
        //response.setContent((Object) a);
        String co = (String) request.getContent();
        ResponsePrimitive r = GroupLocationUtil.retrieveLocalInfo("http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~/" + Constants.CSE_ID);
        List<String> mncse = GroupLocationUtil.retrieveRemoteCse("", (String) r.getContent());
        String content = "<incse name=\"" + Constants.CSE_ID + "\">\n";
        for(String m: mncse) {
            content += getGroupLocation(m);

        }
        content += "</incse>";
        response.setContent((Object) content);
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


    private String getGroupLocation(String mncse) {
        String res = "";
        String poa = "http://" + Constants.CSE_IP + ":" + Constants.CSE_PORT + "/~";
        ResponsePrimitive r = GroupLocationUtil.retrieveLocalInfo(poa + mncse);
        String co = (String) r.getContent();
        r = GroupLocationUtil.retrieveLocalInfo(GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~"
            + GroupLocationUtil.findMatch(GroupLocationUtil.csiPattern, co));

        // mn location
        String locationParameter = GroupLocationUtil.createLocationParameter("hello", "baidu.com", "in-cse", 1,
            GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~"
            + GroupLocationUtil.findMatch(GroupLocationUtil.csiPattern, co));
        String locationContainerl = GroupLocationUtil.createLocationPolicy(1, 12, -1,
            "lcoationame", "xml", "local", locationParameter,
            GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~"
            + GroupLocationUtil.findMatch(GroupLocationUtil.csiPattern, co));
        ResponsePrimitive rcl = GroupLocationUtil.retrieve(GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~" + locationContainerl);
        ResponsePrimitive rdl = GroupLocationUtil.retrieve(GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~" +
            GroupLocationUtil.findMatch(GroupLocationUtil.ch4Pattern, (String) rcl.getContent()));
        String lli = GroupLocationUtil.findMatch(GroupLocationUtil.conPattern, (String) rdl.getContent());
        if(lli.length() != 0) {
            res = " <chcse name=\"" + lli.split(" ")[0] + "\" pos=\"" + lli.split(" ")[1] + "\">\n";
        }
        List<String> members = GroupLocationUtil.retrieveRemoteCse(Constants.CSE_NAME, (String) r.getContent());
        if(members.size() == 0) {
            return res;
        }


        String group = GroupLocationUtil.createGroup(members, GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~"
            + GroupLocationUtil.findMatch(GroupLocationUtil.csiPattern, co));
        // asn location
        String locationContainer = GroupLocationUtil.createLocationPolicy(3, 12, -1,
            "lcoationame", "xml", group, locationParameter,
            GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~"
            + GroupLocationUtil.findMatch(GroupLocationUtil.csiPattern, co));
        ResponsePrimitive rc = GroupLocationUtil.retrieve(GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~" + locationContainer);
        ResponsePrimitive rd = GroupLocationUtil.retrieve(GroupLocationUtil.findMatch(GroupLocationUtil.poaPattern, co) + "~" +
            GroupLocationUtil.findMatch(GroupLocationUtil.ch4Pattern, (String) rc.getContent()));
        String[] li = GroupLocationUtil.findMatch(GroupLocationUtil.conPattern, (String) rd.getContent()).split(":");
        for(int i=0; i<members.size(); i++) {
            res = res + "  <asncse name=\"" + li[i].split(" ")[0] + "\" pos=\"" + li[i].split(" ")[1] + "\"/>\n";
        }
        res += " </chcse>\n";
        return res;
    }
}
