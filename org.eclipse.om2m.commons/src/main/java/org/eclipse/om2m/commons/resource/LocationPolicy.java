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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.15 at 03:56:27 PM CEST 
//

package org.eclipse.om2m.commons.resource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.onem2m.org/xml/protocols}announceableResource">
 *       &lt;sequence>
 *         &lt;element name="locationSource" type="{http://www.onem2m.org/xml/protocols}locationSource"/>
 *         &lt;element name="locationUpdatePeriod" type="{http://www.w3.org/2001/XMLSchema}duration" minOccurs="0"/>
 *         &lt;element name="locationTargetID" type="{http://www.onem2m.org/xml/protocols}nodeID" minOccurs="0"/>
 *         &lt;element name="locationServer" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="locationContainerID" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="locationContainerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationStatus" type="{http://www.onem2m.org/xml/protocols}status"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="childResource" type="{http://www.onem2m.org/xml/protocols}childResourceRef" maxOccurs="unbounded"/>
 *           &lt;choice maxOccurs="unbounded">
 *             &lt;element ref="{http://www.onem2m.org/xml/protocols}subscription"/>
 *           &lt;/choice>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "locationSource", "locationUpdatePeriod",
		"groupID", "LocationName", "locationStatus", "Method", 
                "childResource", "subscription" })
@XmlRootElement(name = "locationPolicy")
public class LocationPolicy extends AnnounceableResource {

	@XmlElement(required = true)
	protected BigInteger locationSource;
	protected Duration locationUpdatePeriod;
	@XmlSchemaType(name = "anyURI")
	protected String groupID;
	protected String locationName;
	@XmlElement(required = true)
	protected BigInteger locationStatus;
	protected String locationMethod;
        protected String locationGroupId;
        
        // TODO: check use or not
        @XmlSchemaType(name = "anyURI")
        protected String locationContainerID;
        protected String locationContainerName;

	/**
	 * Gets the value of the locationSource property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getLocationSource() {
		return locationSource;
	}

	/**
	 * Sets the value of the locationSource property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setLocationSource(BigInteger value) {
		this.locationSource = value;
	}

	/**
	 * Gets the value of the locationUpdatePeriod property.
	 * 
	 * @return possible object is {@link Duration }
	 * 
	 */
	public Duration getLocationUpdatePeriod() {
		return locationUpdatePeriod;
	}

	/**
	 * Sets the value of the locationUpdatePeriod property.
	 * 
	 * @param value
	 *            allowed object is {@link Duration }
	 * 
	 */
	public void setLocationUpdatePeriod(Duration value) {
		this.locationUpdatePeriod = value;
	}

	/**
	 * Gets the value of the locationContainerID property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getContainerID() {
		return locationContainerID;
	}

	/**
	 * Sets the value of the locationContainerID property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setContainerID(String value) {
		this.locationContainerID = value;
	}

        /**
         * Gets the value of the locationContainerName property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getContainerName() {
                return locationContainerName;
        }

        /**
         * Sets the value of the locationContainerName property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setContainerName(String value) {
                this.locationContainerName = value;
        }


	/**
	 * Gets the value of the locationContainerName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * Sets the value of the locationContainerName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLocationName(String value) {
		this.locationName = value;
	}

	/**
	 * Gets the value of the locationStatus property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getLocationStatus() {
		return locationStatus;
	}

	/**
	 * Sets the value of the locationStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setLocationStatus(BigInteger value) {
		this.locationStatus = value;
	}

        /**
         * Gets the value of the locationContainerName property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getLocationMethod() {
                return locationMethod;
        }

        /**
         * Sets the value of the locationContainerName property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setLocationMethod(String value) {
                this.locationMethod = value;
        }

        /**
         * Gets the value of the locationGroupId property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getLocationGroupId() {
                return locationGroupId;
        }

        /**
         * Sets the value of the locationGroupId property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setLocationGroupId(String locationGroupId) {
                this.locationGroupId = locationGroupId;
        }


	/**
	 * Gets the value of the childResource property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the childResource property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getChildResource().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ChildResourceRef }
	 * 
	 * 
	 */
        /*
	public List<ChildResourceRef> getChildResource() {
		if (childResource == null) {
			childResource = new ArrayList<ChildResourceRef>();
		}
		return this.childResource;
	}*/

	/**
	 * Gets the value of the subscription property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the subscription property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSubscription().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Subscription }
	 * 
	 * 
	 */
        /*
	public List<Subscription> getSubscription() {
		if (subscription == null) {
			subscription = new ArrayList<Subscription>();
		}
		return this.subscription;
	}*/

}
