package org.bahmni.module.lisintegration.atomfeed.mappers;

import java.io.IOException;
import java.text.ParseException;

import org.bahmni.module.lisintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.junit.Assert;
import org.junit.Test;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.parser.PipeParser;

public class HL7ORUtoOpenMRSEncounterMapperTest extends OpenMRSMapperBaseTest {

    @Test
    public void testMapWithSingleTestResultORUMessage() throws HL7Exception, IOException, ParseException {
        String msg1 = "MSH|^~\\&||LIS^Laboratory|BahmniEMR|BahmniEMR|||ORU^R01^ORU_R01||P|2.5\r"
                + "ORC|RE|ORD-111|ORD-111\r" + "OBR|1|Absolute Eosinphil Count\r"
                + "OBX|1|NM|^Absolute Eosinphil Count||55|||||||||20210303144943";

        PipeParser pipeParser = new PipeParser();
        pipeParser.setValidationContext(new ca.uhn.hl7v2.validation.impl.NoValidation());
        Message message = pipeParser.parse(msg1);
        ORU_R01 oru = (ORU_R01) message;

        HL7ORUtoOpenMRSEncounterMapper hl7 = new HL7ORUtoOpenMRSEncounterMapper();
        OpenMRSEncounter openMRSEncounter = hl7.map(oru);
        Assert.assertEquals("ORD-111", openMRSEncounter.getOrders().get(0).getOrderNumber());
        Assert.assertEquals((Double) 55.0, openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getValue());
        Assert.assertEquals("Absolute Eosinphil Count",
                openMRSEncounter.getObs().get(0).getConcept().getName().getName());
        Assert.assertEquals("2021-03-03T15:49:43.000+0100", openMRSEncounter.getObs().get(0).getObsDateTime());
    }

}