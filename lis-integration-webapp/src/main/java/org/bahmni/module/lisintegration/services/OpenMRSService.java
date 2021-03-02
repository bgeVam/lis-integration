package org.bahmni.module.lisintegration.services;

import org.bahmni.module.lisintegration.atomfeed.client.ConnectionDetails;
import org.bahmni.module.lisintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSEncounterMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSPatientMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.SampleMapper;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;

@Component
public class OpenMRSService {

    String patientRestUrl = "/openmrs/ws/rest/v1/patient/";

    public OpenMRSEncounter getEncounter(String encounterUrl) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String encounterJSON = webClient.get(URI.create(urlPrefix + encounterUrl));
        return new OpenMRSEncounterMapper(ObjectMapperRepository.objectMapper).map(encounterJSON);
    }

    public OpenMRSPatient getPatient(String patientUuid) throws IOException, ParseException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String patientJSON = webClient.get(URI.create(urlPrefix + patientRestUrl + patientUuid+"?v=full"));
        return new OpenMRSPatientMapper().map(patientJSON);
    }
    public Sample getSample(String conceptUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String labSamplesConceptAPI = "/openmrs/ws/rest/v1/concept/8160a011-3f10-11e4-adec-0800271c1b75?v=full";

        String labSamplesConcept = webClient.get(URI.create(urlPrefix  + labSamplesConceptAPI));
        ObjectMapper labSamplesObjectMapper = ObjectMapperRepository.objectMapper;
        JsonNode labSamplesJSON  = labSamplesObjectMapper.readTree(labSamplesConcept);
        JsonNode samples = labSamplesJSON.path("setMembers");
        JsonNode sampleJSON = null;

        for(JsonNode sample: samples) {
            JsonNode sampleMembers = sample.path("setMembers");
            for (JsonNode sampleMember: sampleMembers) {
                if (conceptUUID.equals(sampleMember.path("uuid").asText())) {
                    sampleJSON = sample.path("name");
                    break;
                }
            }
        }
        return new SampleMapper().map(sampleJSON.toString());
    }

    private String getURLPrefix() {
        org.bahmni.webclients.ConnectionDetails connectionDetails = ConnectionDetails.get();
        String authenticationURI = connectionDetails.getAuthUrl();

        URL openMRSAuthURL;
        try {
            openMRSAuthURL = new URL(authenticationURI);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI);
        }
        return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
    }

}
