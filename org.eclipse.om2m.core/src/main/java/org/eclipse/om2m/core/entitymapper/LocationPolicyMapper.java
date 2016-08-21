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
package org.eclipse.om2m.core.entitymapper;

import java.math.BigInteger;


import org.eclipse.om2m.commons.constants.ResourceType;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ResultContent;
import org.eclipse.om2m.commons.constants.ShortName;
import org.eclipse.om2m.commons.entities.AccessControlPolicyEntity;
import org.eclipse.om2m.commons.entities.GroupEntity;
import org.eclipse.om2m.commons.entities.SubscriptionEntity;
import org.eclipse.om2m.commons.entities.LocationPolicyEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.resource.ChildResourceRef;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.resource.LocationPolicy;

public class LocationPolicyMapper extends EntityMapper<LocationPolicyEntity, LocationPolicy> {

	@Override
	protected LocationPolicy createResource() {
		return new LocationPolicy();
	}

	@Override
	protected void mapAttributes(LocationPolicyEntity entity, LocationPolicy resource) {
		resource.setLocationSource(entity.getLocationSource());
		resource.setLocationUpdatePeriod(entity.getLocationUpdatePeriod());
		resource.setLocationGroupId(entity.getLocationGroupId());
		resource.setLocationMethod(entity.getLocationMethod());
		resource.setLocationName(entity.getLocationName());
                resource.setLocationStatus(entity.getLocationStatus());
		/*for(AccessControlPolicyEntity acpEntity : entity.getAccessControlPolicies()){
			resource.getAccessControlPolicyIDs().add(acpEntity.getResourceID());
		}*/
		if (!entity.getAnnouncedAttribute().isEmpty()) {			
			resource.getAnnouncedAttribute().addAll(entity.getAnnouncedAttribute());
		}
		if (!entity.getAnnounceTo().isEmpty()) {			
			resource.getAnnounceTo().addAll(entity.getAnnounceTo());
		}
	}

	@Override
	protected void mapChildResourceRef(LocationPolicyEntity entity, LocationPolicy resource) {
                // adding container refs
                for (ContainerEntity cnt : entity.getChildCnt()) {
                        ChildResourceRef child = new ChildResourceRef();
                        child.setResourceName(cnt.getName());
                        child.setType(ResourceType.CONTAINER);
                        child.setValue(cnt.getResourceID());
                        resource.getChildResource().add(child);
                }
	}

	@Override
	protected void mapChildResources(LocationPolicyEntity entity, LocationPolicy resource) {
                // adding container refs
                for (ContainerEntity cnt : entity.getChildCnt()) {
                        Container cntRes = new ContainerMapper().mapEntityToResource(cnt, ResultContent.ATTRIBUTES);
                        resource.getContainers().add(cntRes);
                }
	}

}
