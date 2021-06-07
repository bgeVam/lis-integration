package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSVisit;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

public class OpenMRSEncounterMapper {
    private ObjectMapper objectMapper;

    public OpenMRSEncounterMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     *This method is used to map the encounter to a {@link OpenMRSEcnounter}.
     *
     * @param encounterJSON is the json containg the info we need to map.
     * @return openMRSEncounter returns the {@link OpenMRSEncounter}
     *         populated with the data form the encounterJSON.
     * @throws IOException if the String cannot be read via {@link #readTree(String)} method.
     */
    public OpenMRSEncounter map(String encounterJSON) throws IOException {
        OpenMRSEncounter openMRSEncounter = objectMapper.readValue(encounterJSON, OpenMRSEncounter.class);
        ObjectMapper encounterObjectMapper = ObjectMapperRepository.objectMapper;
        JsonNode enconterJsonNode = encounterObjectMapper.readTree(encounterJSON);

        String visitUuid = enconterJsonNode.path("visitUuid").asText();
        OpenMRSVisit openMRSVisit = new OpenMRSVisit();
        openMRSVisit.setUuid(visitUuid);

        openMRSEncounter.setVisit(openMRSVisit);

        return openMRSEncounter;
    }
}
