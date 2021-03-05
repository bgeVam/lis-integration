package org.bahmni.module.lisintegration.atomfeed.mappers;

import java.io.IOException;
import java.text.ParseException;

import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.XMLParser;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.map.ObjectMapper;


import ca.uhn.hl7v2.model.v25.segment.ORC;
// import ca.uhn.hl7v2.model.Message;
// import ca.uhn.hl7v2.model.v25.message.ORU_R01;
// import ca.uhn.hl7v2.parser.PipeParser;

import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.group.*;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.ORC;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.Varies;
// import org.junit.BeforeClass;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;

public class HL7ORUtoOpenMRSEncounterMapper {
    private ObjectMapper objectMapper;

    public HL7ORUtoOpenMRSEncounterMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public OpenMRSEncounter map(String HL7ORU) throws IOException, ParseException, HL7Exception{
        // Create Message from ORU Example

        String msg1 = "MSH|^~\\&||LIS^Laboratory|BahmniEMR|BahmniEMR|||ORU^R01^ORU_R01||P|2.5\r"
                + "ORC|RE|ORD-111|ORD-111\r"
                + "OBR|1|Absolute Eosinphil Count\r"
                + "OBX|1|NM|^Absolute Eosinphil Count||55|||||||||20210303144943";

        PipeParser pipeParser = new PipeParser();
        pipeParser.setValidationContext(new ca.uhn.hl7v2.validation.impl.NoValidation());
        Message message = pipeParser.parse(msg1);
        ORU_R01 oru = (ORU_R01) message;
        
        for (ORU_R01_PATIENT_RESULT response : oru.getPATIENT_RESULTAll()) {
          for (ORU_R01_ORDER_OBSERVATION orderObservation : response.getORDER_OBSERVATIONAll()) {
            //OBR segment can be used for panel
            OBR obr = orderObservation.getOBR();

            ORC orc = orderObservation.getORC();
            String entityIdentifier = orc.getFillerOrderNumber().getEntityIdentifier().getValue();

            for (ORU_R01_OBSERVATION observation : orderObservation.getOBSERVATIONAll()) {
              OBX obx = observation.getOBX();
              String type = obx.getObx3_ObservationIdentifier().getCe2_Text().getValue();
              String status = obx.getObservationResultStatus().getValue();

              for (Varies varies : obx.getObx5_ObservationValue()) {
                String value = varies.encode();
              }
            }
        }
    }
        return null;
    }

}
