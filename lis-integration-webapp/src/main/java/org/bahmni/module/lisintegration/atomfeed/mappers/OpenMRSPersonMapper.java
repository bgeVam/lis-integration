package org.bahmni.module.lisintegration.atomfeed.mappers;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSPerson;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class OpenMRSPersonMapper {
    private ObjectMapper objectMapper;

    public OpenMRSPersonMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    /**
     * This method is called to map the personJson response to a {@link OpenMRSPerson}
     *
     * @param personJSON is the json API response we get from the API
     * @return person returns a constructed {@link OpenMRSPerson} with the desired
     * data from the input paramater
     * @throws IOException
     */
    public OpenMRSPerson map(String personJSON) throws IOException {
        OpenMRSPerson person = new OpenMRSPerson();
        JsonNode jsonNode = objectMapper.readTree(personJSON);

        person.setPersonUUID(jsonNode.path("uuid").asText());
        person.setGivenName(jsonNode.path("preferredName").path("givenName").asText());
        person.setFamilyName(jsonNode.path("preferredName").path("familyName").asText());

        return person;
    }
}

