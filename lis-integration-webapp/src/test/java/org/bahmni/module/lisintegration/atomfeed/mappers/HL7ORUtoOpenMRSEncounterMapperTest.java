package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.mappers.HL7ORUtoOpenMRSEncounterMapper;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class HL7ORUtoOpenMRSEncounterMapperTest extends OpenMRSMapperBaseTest {
	// @Autowired
 //    private HL7ORUtoOpenMRSEncounterMapper HL7ORUtoOpenMRSEncounterMapper;


    @Test
    public void test() {

    	HL7ORUtoOpenMRSEncounterMapper hl7 = new HL7ORUtoOpenMRSEncounterMapper();
    	try{
        OpenMRSEncounter openMRSEncounter = hl7.map("asdd"); 
        // openMRSEncounter.setPatientUuid("BahmniEMR");
        Assert.assertEquals("LIS", openMRSEncounter.getPatientUuid());
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }


}