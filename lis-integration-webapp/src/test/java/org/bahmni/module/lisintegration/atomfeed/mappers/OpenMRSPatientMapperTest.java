package org.bahmni.module.lisintegration.atomfeed.mappers;

import junit.framework.Assert;
import org.bahmni.module.lisintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.junit.Test;

public class OpenMRSPatientMapperTest extends OpenMRSMapperBaseTest {

    @Test
    public void testMap() throws Exception {
        String json = deserialize("/samplePatient.json");
        OpenMRSPatient patient = new OpenMRSPatientMapper().map(json);

        Assert.assertEquals("GAN200053", patient.getPatientId());
        Assert.assertEquals("TestDrivingLicence", patient.getDrivingLicenseNumber());
        Assert.assertEquals("TestSSNNumber", patient.getSSNNumber());
        Assert.assertEquals(" T e s t N o O p e_n-V i s i t ", patient.getGivenName());
        Assert.assertEquals("S c e n a r i o 9", patient.getFamilyName());
        Assert.assertEquals("F", patient.getGender());
//        Assert.assertEquals("Wed Jun 12 00:15:00 NPT 1996", patient.getBirthDate().toString());

    }
}