package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ResultMapper {
    private final ObjectMapper objectMapper;

    public ResultMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public ResultEncounter map(OpenMRSEncounter openMRSEncounter) throws IOException {
        ResultEncounter result = new ResultEncounter();

        EncounterProvider encounterProvider = new EncounterProvider();
        Observation observation = new Observation();
        Observation testChild = new Observation();

        encounterProvider.setProvider(openMRSEncounter.getProviders().get(0).getUuid());
        encounterProvider.setEncounterRole(openMRSEncounter.getEncounterRole());

        observation.setConcept(openMRSEncounter.getObs().get(0).getConcept().getUuid());
        observation.setObsDatetime(openMRSEncounter.getObs().get(0).getObsDateTime());
        observation.setOrder(openMRSEncounter.getObs().get(0).getOrder().getUuid());

        // set GroupMembers for panel
        if (openMRSEncounter.isPanel()) {
            int countTests = openMRSEncounter.getObs().get(0).getGroupMembers().size();
            List<Observation> sourceArray = new ArrayList<Observation>(countTests);

            for (int test = 0; test < countTests; test++) {
                Observation panelChild = new Observation();
                Observation panelChildOfChild = new Observation();
                panelChild.setConcept(
                    openMRSEncounter.getObs().get(0).getGroupMembers().get(test).getConcept().getUuid());
                panelChild.setOrder(openMRSEncounter.getObs().get(0).getOrder().getUuid());
                panelChild.setObsDatetime(openMRSEncounter.getObs().get(0).getObsDateTime());
                panelChildOfChild.setConcept(
                    openMRSEncounter.getObs().get(0).getGroupMembers().get(test).getConcept().getUuid());
                panelChildOfChild.setValue(openMRSEncounter.getObs().get(0).getGroupMembers().get(test).getValue());
                panelChild.setGroupMembers(Arrays.asList(panelChildOfChild));
                sourceArray.add(panelChild);
            }
            observation.setGroupMembers(sourceArray);
        // set GroupMembers for test
        } else {
            testChild.setConcept(openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getConcept().getUuid());
            testChild.setValue(openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getValue());

            observation.setGroupMembers(Arrays.asList(testChild));
        }

        result.setPatient(openMRSEncounter.getPatientUuid());
        result.setObs(Arrays.asList(observation));
        result.setEncounterType(openMRSEncounter.getEncounterType());
        result.setVisit(openMRSEncounter.getVisit().getUuid());
        result.setEncounterProviders(Arrays.asList(encounterProvider));

        return result;
    }
}
