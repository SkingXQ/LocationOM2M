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
 * Location Policy JPA entity
 *
 */
@Entity(name=DBEntities.LOCATIONPOLICY_ENTITY)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class LocationPolicyEntity extends AnnounceableSubordinateEntity {

    @Column(name = ShortName.LOCATIONRESOURCE)
    protected BigInteger locationSource;
    
    @Column(name = ShortName.LOCATIONUPDATEPERIOD)
    protected String locationUpdatePeriod;
   
    // TODO: check locationGroupId or gourp id as resource ?
    @Column(name = ShortName.LOCATIONGROUPID)
    protected String locationGroupId;
 
    // TODO: locationName or name  
    @Column(name = ShortName.LOCATIONNAME)
    protected String locationName;

    // TODO: locationMethod or method?
    @Column(name = ShortName.LOCATIONMETHOD)
    protected String locationMethod;

    @Column(name = ShortName.LOCATIONSTATUS)
    protected BigInteger locationStatus;
    // database link to parent CSEBase
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = CSEBaseEntity.class)
    @JoinTable(
        name = DBEntities.CSEB_LOCP_JOIN,
        joinColumns = { @JoinColumn(name = DBEntities.LOCP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) },
        inverseJoinColumns = { @JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
        )
    protected CSEBaseEntity parentCseBase;
 //   @ManyToOne(fetch = FetchType.LAZY, targetEntity n= CSEBaseEntity.class)
   // @JoinTable(
     //   name = DBEntities.CSEB_LOCP_JOIN,
       /// joinColumns = { @JoinColumn(name = DBEntities.LOCP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) },  
       // inverseJoinColumns = { @JoinColumn(name = DBEntities.CSEB_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
       // )   
    //protected CSEBaseEntity parentCseBase;

    // list of child CNT
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = DBEntities.LOCP_CHCNT_JOIN,
            joinColumns = { @JoinColumn(name = DBEntities.LOCP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) },
            inverseJoinColumns = { @JoinColumn(name = DBEntities.CNT_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
            )
    protected List<ContainerEntity> childCnt;
 

    // database link to location parameter
    /*@ManyToOne(fetch = FetchType.LAZY, targetEntity = LocationParameterEntity.class)
    @JoinTable(
        name = DBEntities.LOCPA_LOCP_JOIN,
        joinColumns = { @JoinColumn(name = DBEntities.LOCPA_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) },  
        inverseJoinColumns = { @JoinColumn(name = DBEntities.LOCP_JOIN_ID, referencedColumnName = ShortName.RESOURCE_ID) }
        )   
    protected LocationParameter locationParameter;*/
    
    // get and set locationSource
    public BigInteger getLocationSource() {
        return locationSource;
    }   

    public void setLocationSource(BigInteger locationSource) {
        this.locationSource = locationSource;
    }

    // get and set location update period
    public String getLocationUpdatePeriod() {
        return this.locationUpdatePeriod;
        //return locationUpdatePeriod;
    }

    public void setLocationUpdatePeriod(String locationUpdatePeriod) {
        this.locationUpdatePeriod = locationUpdatePeriod;
    }
    
    // get and set location group id
    public String getLocationGroupId() {
        return locationGroupId;
    }

    public void setLocationGroupId(String locationGroupId) {
        this.locationGroupId = locationGroupId;
    }

    // get and set location name
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
 
    // get and set location method
    public String getLocationMethod() {
        return locationMethod;
    }

    public void setLocationMethod(String locationMethod) {
        this.locationMethod = locationMethod;
    }

    // get and set location status
    public BigInteger getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(BigInteger locationStatus) {
        this.locationStatus = locationStatus;
    }
    
    // get and set parent CSEBase
    public CSEBaseEntity getParentCseBase() {
        return parentCseBase;
    }
   
    public void setParentCseBase(CSEBaseEntity  parentCseBase) {
        this.parentCseBase = parentCseBase;
    }

    /** 
     * @return the childCnt
     */
    public List<ContainerEntity> getChildCnt() {
        if (this.childCnt == null) {
            this.childCnt = new ArrayList<>();
        }   
        return childCnt;
    }   

    /** 
     * @param childCnt the childCnt to set
     */
    public void setChildCnt(List<ContainerEntity> childCnt) {
        this.childCnt = childCnt;
    }   

}
