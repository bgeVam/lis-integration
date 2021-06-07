package org.bahmni.module.lisintegration.atomfeed.mappers;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteMapper {
    private final ObjectMapper objectMapper;

    @Value("${concept.accession_uuid}")
    private String conceptAccessionUuid;

    @Value("${concept.accession_note}")
    private String conceptAccessionNote;

    @Value("${encounter.type.validation_note}")
    private String encounterValidationUuid;

    public NoteMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }
    /**
     * The method map gathers the informations needed for note
     *
     * @param openMRSEncounter  used to fetch the encounter data
     * @param encounterJSONNode used to fetch the location uuid in JSONNode format
     * @param conceptAccessionNote used to fetch the uuid for accession note
     * @param encounterValidationUuid used to fetch the Validation Encounter uuid
     * @return generatenote object
     */
    public NoteEncounter map(OpenMRSEncounter openMRSEncounter, JsonNode encounterJSONNode,
    String conceptAccessionNote, String encounterValidationUuid) throws IOException {
        NoteEncounter result = new NoteEncounter();

        EncounterProvider encounterProvider = new EncounterProvider();
        List<EncounterProvider> encounterProviders = new ArrayList<>();

        result.setPatient(openMRSEncounter.getPatientUuid());
        result.setEncounterType(encounterValidationUuid);
        encounterProvider.setProvider(openMRSEncounter.getProviders().get(0).getUuid());
        encounterProvider.setEncounterRole(openMRSEncounter.getEncounterRole());
        encounterProviders.add(encounterProvider);
        result.setEncounterProviders(Arrays.asList(encounterProvider));
        result.setVisit(openMRSEncounter.getVisit().getUuid());
        result.setEncounterDatetime(openMRSEncounter.getEncounterDatetime());
        result.setLocation(encounterJSONNode.path("location").path("uuid").getTextValue());

        String dateTime = openMRSEncounter.getObs().get(0).getObsDateTime();
        OpenMRSObs accessionNote = openMRSEncounter.getOpenMRSObs().get(0);
        OpenMRSObs accessionUuid = openMRSEncounter.getOpenMRSObs().get(1);

        Observation obsAccessionUuid = new Observation();
        obsAccessionUuid.setConcept(accessionUuid.getConcept().getUuid());
        obsAccessionUuid.setObsDatetime(dateTime);
        obsAccessionUuid.setValueText(accessionUuid.getValueText());
        Observation obsAccessionNote = new Observation();
        obsAccessionNote.setConcept(conceptAccessionNote);
        obsAccessionNote.setObsDatetime(dateTime);
        obsAccessionNote.setValueText(accessionNote.getValueText());

        List<Observation> listobservation = new ArrayList<Observation>(2);
        listobservation.add(obsAccessionUuid);
        listobservation.add(obsAccessionNote);
        result.setObs(listobservation);

        return result;
    }
}
