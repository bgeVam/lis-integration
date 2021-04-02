package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.junit.Test;


import junit.framework.Assert;

public class OrderMapperTest extends OpenMRSMapperBaseTest {
    
    @Test
    public void testIfMapperProducesCorrectResult() throws Exception {
        /**
        * This is a test of function `map` in class `OrderMapper`
        * 
        * To pass successfully the test next conditions should be true:
        * 1. Order UUID value should be equal with `5330e5d0-f134-45d4-8615-f5462492481e`
        * 2. Concept UUID value should be equel with `4e905b9d-83f6-43c6-b388-0e1f9490c39b`
        * 3. Object of concept should be not null
        * 4. Object of patient taken from the method should not be same as the object patient created in the function 
        */
        String patientUuid = "uuid-test-uuid-test";
        String json = deserialize("/order.json");

        OpenMRSPatient patient = new OpenMRSPatient();
        patient.setPatientUUID(patientUuid);
        OpenMRSOrder order = new OrderMapper().map(json);

        Assert.assertEquals("5330e5d0-f134-45d4-8615-f5462492481e", order.getUuid());
        Assert.assertEquals("4e905b9d-83f6-43c6-b388-0e1f9490c39b", order.getConcept().getUuid());
        Assert.assertNotNull(order.getConcept());
        Assert.assertNotSame(order.getPatient(), patient);
    }
}
