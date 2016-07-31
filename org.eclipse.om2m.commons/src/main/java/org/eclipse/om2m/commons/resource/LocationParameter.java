// Author: sking


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
 *         &lt;element name="locationTargetID" type="{http://www.onem2m.org/xml/protocols}nodeID" minOccurs="0"/>
 *         &lt;element name="locationServer" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="locationContainerID" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="locationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "locationTargetID", "locationServer", 
                "locationContainerID", "locationName", 
                "locationStatus", "childResource",
                "subscription" })
@XmlRootElement(name = "locationParameter")
public class LocationParameter extends AnnounceableResource {


        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String locationTargetID;
        @XmlSchemaType(name = "anyURI")
        protected String locationServer;
        // remove the container part
        @XmlSchemaType(name = "anyURI")
        protected String locationContainerID;
        protected String locationName;
        @XmlElement(required = true)
        protected BigInteger locationStatus;
        protected List<ChildResourceRef> childResource;
        @XmlElement(namespace = "http://www.onem2m.org/xml/protocols")
        protected List<Subscription> subscription;

        /**
         * Gets the value of the locationTargetID property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getLocationTargetID() {
                return locationTargetID;
        }

        /**
         * Sets the value of the locationTargetID property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setLocationTargetID(String value) {
                this.locationTargetID = value;
        }

        /**
         * Gets the value of the locationServer property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getLocationServer() {
                return locationServer;
        }

        /**
         * Sets the value of the locationServer property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setLocationServer(String value) {
                this.locationServer = value;
        }

        /**
         * Gets the value of the locationContainerID property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getLocationContainerID() {
                return locationContainerID;
        }

        /**
         * Sets the value of the locationContainerID property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setLocationContainerID(String value) {
                this.locationContainerID = value;
        }

        /**
         * Gets the value of the locationContainerName property.
         * 
         * @return possible object is {@link String }
         * 
         */
        public String getLocationName() {
                return locationContainerName;
        }

        /**
         * Sets the value of the locationContainerName property.
         * 
         * @param value
         *            allowed object is {@link String }
         * 
         */
        public void setLocationName(String value) {
                this.locationContainerName = value;
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
        public List<ChildResourceRef> getChildResource() {
                if (childResource == null) {
                        childResource = new ArrayList<ChildResourceRef>();
                }
                return this.childResource;
        }

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
        public List<Subscription> getSubscription() {
                if (subscription == null) {
                        subscription = new ArrayList<Subscription>();
                }
                return this.subscription;
        }


}
