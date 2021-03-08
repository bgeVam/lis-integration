package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class OrderMapper {
    private final ObjectMapper objectMapper;

    public OrderMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public OpenMRSOrder map(String orderJSON) throws IOException {

        JsonNode orderJSONNode = objectMapper.readTree(orderJSON);

        OpenMRSOrder order = new OpenMRSOrder();
        OpenMRSPatient patient = new OpenMRSPatient();
        OpenMRSEncounter encounter = new OpenMRSEncounter();
        OpenMRSConcept concept = new OpenMRSConcept();

        patient.setPatientUUID(orderJSONNode.path("patient").path("uuid").getTextValue());
        concept.setUuid(orderJSONNode.path("concept").path("uuid").getTextValue());
        encounter.setEncounterUuid(orderJSONNode.path("encounter").path("uuid").getTextValue());
        order.setUuid(orderJSONNode.path("uuid").getTextValue());
        order.setPatient(patient);
        order.setEncounter(encounter);
        order.setConcept(concept);

        return order;
    }
}
