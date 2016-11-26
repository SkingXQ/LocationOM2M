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
import org.eclipse.om2m.commons.entities.LocationParameterEntity;
import org.eclipse.om2m.commons.entities.ContainerEntity;
import org.eclipse.om2m.commons.resource.ChildResourceRef;
import org.eclipse.om2m.commons.resource.Group;
import org.eclipse.om2m.commons.resource.Subscription;
import org.eclipse.om2m.commons.resource.LocationParameter;
import org.eclipse.om2m.commons.resource.Container;

public class LocationParameterMapper extends EntityMapper<LocationParameterEntity, LocationParameter> {

	@Override
	protected LocationParameter createResource() {
		return new LocationParameter();
	}

	@Override
	protected void mapAttributes(LocationParameterEntity entity, LocationParameter resource) {
		resource.setLocationTargetID(entity.getLocationTargetID());
		resource.setLocationServer(entity.getLocationServer());
		resource.setLocationStatus(entity.getLocationStatus());
		resource.setLocationName(entity.getLocationName());
                resource.setLocationSource(entity.getLocationSource());
		resource.setLocationContainerID(entity.getLocationContainerID());
		if (!entity.getAnnouncedAttribute().isEmpty()) {			
			resource.getAnnouncedAttribute().addAll(entity.getAnnouncedAttribute());
		}
		if (!entity.getAnnounceTo().isEmpty()) {			
			resource.getAnnounceTo().addAll(entity.getAnnounceTo());
		}
	}

	@Override
	protected void mapChildResourceRef(LocationParameterEntity entity, LocationParameter resource) {
                // adding container refs
                for (SubscriptionEntity sub : entity.getSubscriptions()) {
                        ChildResourceRef child = new ChildResourceRef();
                        child.setResourceName(sub.getName());
                        child.setType(ResourceType.CONTAINER);
                        child.setValue(sub.getResourceID());
                        resource.getChildResource().add(child);
                }
	}

	@Override
	protected void mapChildResources(LocationParameterEntity entity, LocationParameter resource) {
                // adding subscription refs
                for (SubscriptionEntity sub : entity.getSubscriptions()){
                        Subscription subRes = new SubscriptionMapper().mapEntityToResource(sub, ResultContent.ATTRIBUTES);
                        resource.getSubscriptions().add(subRes);
                }

	}

}
