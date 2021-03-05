package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class ORUHandler implements ReceivingApplication {
    private static final org.apache.log4j.Logger log = Logger.getLogger(ORUHandler.class);

    @Override
    public Message processMessage(Message message, Map<String, Object> stringObjectMap) throws ReceivingApplicationException, HL7Exception {
        try {
            log.info(message.encode());
            log.info("--------------------");

            ORU_R01 oruR01 = (ORU_R01) message;

            String encodedMessage = new PipeParser().encode(message);
            log.info("Received message:\n" + encodedMessage + "\n\n");
            
            // TODO Insert Observation OpenMRSEncounter here

            return message.generateACK();
        } catch(Throwable t) {
            log.error("Throwable caught: ", t);
            throw new ReceivingApplicationException(t);
        }
    }

    @Override
    public boolean canProcess(Message message) {
        log.info("ORUHandler.canProcess");
        log.info(message);
        return true;
    }
}
