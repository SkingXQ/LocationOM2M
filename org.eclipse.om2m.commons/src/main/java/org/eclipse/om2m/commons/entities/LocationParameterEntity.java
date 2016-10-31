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
package org.eclipse.om2m.commons.entities;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.datatype.Duration;

import org.eclipse.om2m.commons.constants.DBEntities;
import org.eclipse.om2m.commons.constants.ShortName;


/**
 * Location Parameter JPA entity
 *
 */
@Entity(name=DBEntities.LOCATIONPARAMETER_ENTITY)
public class LocationParameterEntity extends AnnounceableSubordinateEntity {


    @Column(name = ShortName.LOCATIONTARGETID)
    protected String locationTargetID;
    @Column(name = ShortName.LOCATIONSERVER)
    protected String locationServer;
    @Column(name = ShortName.LOCATIONSTATUS)
    protected BigInteger locationStatus;
    // TODO: locationName or name  
    @Column(name = ShortName.LOCATIONNAME)
    protected String locationName;
 
    @Column(name = ShortName.LOCATIONCNTID)
    protected String locationContainerID;

    // database link to parent CSEBase
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = CSEBaseEntity.class)
    @JoinTable(
        name = DBEntities.CSEB_LOCPA_JOIN,
        joinColumns = { @JoinColumn(name = DBEntities.LOCPA_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) },
        inverseJoinColumns = { @JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
        )
    protected CSEBaseEntity parentCseBase;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinTable(
            name = DBEntities.LOCPA_SUB_JOIN,
            joinColumns = { @JoinColumn(name = DBEntities.LOCPA_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) },
            inverseJoinColumns = { @JoinColumn(name = DBEntities.SUB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
            )
    protected List<SubscriptionEntity> subscription;

    /**
     * @return the location name 
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * @param locationName
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    /**
     * @return the location Container id
     */
    public String getLocationContainerID() {
        return locationContainerID;
    }

    /**
     * @param locationContainid
     */
    public void setLocationContainerID(String locationContainerID) {
        this.locationContainerID = locationContainerID;
    }



    /**
     * @return the location target id
     */
    public String getLocationTargetID() {
        return locationTargetID;
    }
    
    /**
     * @param locationtargetid
     */
    public void setLocationTargetID(String locationTargetID) {
        this.locationTargetID = locationTargetID;
    }

    /**
     * @return location server
     */
    public String getLocationServer() {
        return locationServer;
    }

    /**
     * @param locationServer
     */
     public void setLocationServer(String locationServer) {
         this.locationServer = locationServer;
     }

    /** 
     * @return location status
     */
    public BigInteger getLocationStatus() {
        return locationStatus;
    }   

    /** 
     * @param locationStatus
     */
     public void setLocationStatus(BigInteger locationStatus) {
         this.locationStatus = locationStatus;
     }   
    
    /**
     * @return the subscriptions
     */
    public List<SubscriptionEntity> getSubscriptions() {
        if (this.subscription == null) {
            this.subscription = new ArrayList<SubscriptionEntity>();
        }
        return subscription;
    }


}
