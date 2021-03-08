package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;


public class ResultMapper {
    private final ObjectMapper objectMapper;

    public ResultMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public ResultEncounter map(OpenMRSEncounter openMRSEncounter) throws IOException {
        ResultEncounter result = new ResultEncounter();

        EncounterProvider encounterProvider = new EncounterProvider();
        Observation observation = new Observation();
        Observation obsChild = new Observation();

        encounterProvider.setProvider(openMRSEncounter.getProviders().get(0).getUuid());
        encounterProvider.setEncounterRole(openMRSEncounter.getEncounterRole());

        observation.setConcept(openMRSEncounter.getObs().get(0).getConcept().getUuid());
        observation.setObsDatetime(openMRSEncounter.getObs().get(0).getObsDateTime());
        observation.setOrder(openMRSEncounter.getObs().get(0).getOrder().getUuid());

        obsChild.setConcept(openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getConcept().getUuid());
        obsChild.setValue(openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getValue());

        observation.setGroupMembers(Arrays.asList(obsChild));

        result.setPatient(openMRSEncounter.getPatientUuid());
        result.setObs(Arrays.asList(observation));
        result.setEncounterType(openMRSEncounter.getEncounterType());
        result.setVisit(openMRSEncounter.getVisit().getUuid());
        result.setEncounterProviders(Arrays.asList(encounterProvider));

        return result;
    }
}
