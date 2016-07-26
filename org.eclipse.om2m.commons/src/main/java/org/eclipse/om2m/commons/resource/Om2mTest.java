package org.eclipse.om2m.commons.resource;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.Duration;
import javax.xml.datatype.DatatypeFactory;
import org.eclipse.om2m.commons.resource.LocationPolicy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Om2mTest {
    public static void main(String[] args) {
        LocationPolicy test = new LocationPolicy();
        test.setLocationSource(BigInteger.valueOf(10));
        test.setLocationTargetID("sking");
        test.setLocationServer("www.google.com");
        test.setLocationContainerID("skingcong");
        test.setLocationContainerName("skingcn");
        test.setLocationStatus(BigInteger.valueOf(1));
	System.out.println(test.getLocationTargetID());
	System.out.println(LocationPolicy.class);
	try{
	    test.setLocationUpdatePeriod(javax.xml.datatype.DatatypeFactory.newInstance().newDuration("00:00:36,800"));
        } catch(Exception e){
	}
       // test.childResource = null;
       // test.subscription = null;
        try {
            File file = new File("~/test.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(LocationPolicy.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(test, file);
            jaxbMarshaller.marshal(test, System.out);
        } catch(JAXBException e) {
            e.printStackTrace();
        }

    }
}

