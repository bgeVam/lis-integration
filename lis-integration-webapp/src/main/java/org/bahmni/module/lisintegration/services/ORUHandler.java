package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import org.apache.log4j.Logger;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.module.lisintegration.atomfeed.mappers.HL7ORUtoOpenMRSEncounterMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.ResultMapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;
import java.util.Map;

public class ORUHandler implements ReceivingApplication {
    private static final org.apache.log4j.Logger log = Logger.getLogger(ORUHandler.class);

    @Override
    public Message processMessage(Message message, Map<String, Object> stringObjectMap) throws ReceivingApplicationException, HL7Exception {
        try {
            final String encounterTypeUUID="82024e00-3f10-11e4-adec-0800271c1b75";
            final String encounterRoleUUID="a0b03050-c99b-11e0-9572-0800200c9a66";
            final String providerUUID="7d162c29-3f12-11e4-adec-0800271c1b75";

            log.info(message.encode());
            log.info("--------------------");

            ORU_R01 oruR01 = (ORU_R01) message;

            String encodedMessage = new PipeParser().encode(message);
            log.info("Received message:\n" + encodedMessage + "\n\n");
            
            HL7ORUtoOpenMRSEncounterMapper mapper = new HL7ORUtoOpenMRSEncounterMapper();
            OpenMRSEncounter openMRSEncounter = new HL7ORUtoOpenMRSEncounterMapper().map(oruR01);
            //Update all uuid information in the encounter

            OpenMRSService service = new OpenMRSService();
            OpenMRSOrder openMRSOrder = service.getOrder(openMRSEncounter.getOrders().get(0).getUuid());

            OpenMRSProvider provider = new OpenMRSProvider();
            provider.setUuid(providerUUID);

            openMRSEncounter.setOrders(Arrays.asList(openMRSOrder));
            openMRSEncounter.setEncounterType(encounterTypeUUID);
            openMRSEncounter.setEncounterRole(encounterRoleUUID);
            openMRSEncounter.setEncounterUuid(openMRSOrder.getEncounter().getEncounterUuid());

            String orderEncounter = service.getEncounterByUUID(openMRSEncounter.getEncounterUuid());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode encounterJSONNode = objectMapper.readTree(orderEncounter);
            openMRSEncounter.setPatientUuid(openMRSEncounter.getOrders().get(0).getPatient().getPatientUUID());
            openMRSEncounter.setProviders(Arrays.asList(provider));

            Visit visit = new Visit();
            visit.setUuid(encounterJSONNode.path("visit").path("uuid").getTextValue());

            openMRSEncounter.setVisit(visit);
            openMRSEncounter.getObs().get(0).getConcept().setUuid(openMRSOrder.getConcept().getUuid());
            openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getConcept().setUuid(openMRSOrder.getConceptUUID());
            ResultEncounter resultEncounter1 = new ResultMapper().map(openMRSEncounter);

            service.postResultEncounter(resultEncounter1);

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
