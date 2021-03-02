package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class SampleMapper {
    private ObjectMapper objectMapper;

    public SampleMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public Sample map(String sampleJSON) throws IOException {
        Sample sample = new Sample();
        JsonNode sampleJSONNode = objectMapper.readTree(sampleJSON);
        sample.setName(sampleJSONNode.path("name").asText());

        return sample;
    }
}
