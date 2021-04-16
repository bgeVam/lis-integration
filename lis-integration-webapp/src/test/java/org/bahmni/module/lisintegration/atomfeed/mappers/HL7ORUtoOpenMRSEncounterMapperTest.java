package org.bahmni.module.lisintegration.atomfeed.mappers;

import java.io.IOException;
import java.text.ParseException;

import javax.naming.AuthenticationException;

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
                + "ORC|RE|ORD-123|6b519258-c97b-4c6b-891b-e6f478123cbb\r"
                + "OBR|1|||^Absolute Eosinphil Count\r"
                + "OBX|1|NM|^Absolute Eosinphil Count||55|||||||||20210303144943";

        PipeParser pipeParser = new PipeParser();
        pipeParser.setValidationContext(new ca.uhn.hl7v2.validation.impl.NoValidation());
        Message message = pipeParser.parse(msg1);
        ORU_R01 oru = (ORU_R01) message;

        HL7ORUtoOpenMRSEncounterMapper hl7 = new HL7ORUtoOpenMRSEncounterMapper();
        OpenMRSEncounter openMRSEncounter = hl7.map(oru);
        Assert.assertEquals("6b519258-c97b-4c6b-891b-e6f478123cbb", openMRSEncounter.getOrders().get(0).getUuid());
        Assert.assertEquals((Double) 55.0, openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getValue());
        Assert.assertEquals("Absolute Eosinphil Count",
                openMRSEncounter.getObs().get(0).getConcept().getName().getName());
        Assert.assertEquals("2021-03-03T15:49:43.000+0100", openMRSEncounter.getObs().get(0).getObsDateTime());
    }
    @Test
    public void testPostResultOfAPanelToOpenMRS()
            throws HL7Exception, IOException, ParseException, AuthenticationException {
        String msg1 = "MSH|^~\\&||LIS^Laboratory|BahmniEMR|BahmniEMR|||ORU^R01^ORU_R01||P|2.5\r"
                + "ORC|RE|ORD-310|b4ee913c-5262-4d63-ba68-c1a66f8a2bae||||^^^^^R\r"
                + "OBR|1|||^Hemogram without Platelets^^^Panel\r" + "OBX|1|NM|^Leukocytes||33|||||||||20210412115704\r"
                + "OBX|2|NM|^MCV [Entitic volume]||7|||||||||20210412115704\r"
                + "OBX|3|NM|^Erythrocyte distribution width [Ratio]||21|||||||||20210412115704\r"
                + "OBX|4|NM|^Erythrocytes||22|||||||||20210412115704\r"
                + "OBX|5|NM|^Hemoglobin [Mass/volume]||23|||||||||20210412115705\r"
                + "OBX|6|NM|^Hematocrit [Volume Fraction]||11|||||||||20210412115704\r"
                + "OBX|7|NM|^MCH [Entitic mass]||7|||||||||20210412115704\r"
                + "OBX|8|NM|^MCHC [Mass/volume]||78|||||||||20210412115704\r"
                + "OBX|9|NM|^Erythrocyte distribution width [Entitic volume]||2|||||||||20210412115705\r";

        PipeParser pipeParser = new PipeParser();
        pipeParser.setValidationContext(new ca.uhn.hl7v2.validation.impl.NoValidation());
        Message message = pipeParser.parse(msg1);
        ORU_R01 oru = (ORU_R01) message;

        HL7ORUtoOpenMRSEncounterMapper hl7 = new HL7ORUtoOpenMRSEncounterMapper();
        OpenMRSEncounter openMRSEncounter = hl7.map(oru);
        Assert.assertEquals("b4ee913c-5262-4d63-ba68-c1a66f8a2bae", openMRSEncounter.getOrders().get(0).getUuid());
        Assert.assertEquals("2021-04-12T13:57:05.000+0200", openMRSEncounter.getObs().get(0).getObsDateTime());
        Assert.assertEquals((Double) 33.0, openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getValue());
        Assert.assertEquals("Leukocytes",
                openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getConcept().getName().getName());
    }
}
