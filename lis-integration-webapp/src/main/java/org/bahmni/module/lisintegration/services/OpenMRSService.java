package org.bahmni.module.lisintegration.services;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bahmni.module.lisintegration.atomfeed.client.ConnectionDetails;
import org.bahmni.module.lisintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.ResultEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSEncounterMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSPatientMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.OrderMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.SampleMapper;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import org.apache.log4j.Logger;

@Component
public class OpenMRSService {

    String patientRestUrl = "/openmrs/ws/rest/v1/patient/";
    private static final Logger log = Logger.getLogger(OpenMRSService.class);

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

    public OpenMRSOrder getOrder(String orderUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String orderAPI = "/openmrs/ws/rest/v1/order/"+ orderUUID;
        String orderJSON = webClient.get(URI.create(urlPrefix  + orderAPI));

        return new OrderMapper().map(orderJSON);
    }

    public String getEncounterByUUID(String encounterUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();


        String encounterAPI = "/openmrs/ws/rest/v1/encounter/"+ encounterUUID;
        String encounterJSON = webClient.get(URI.create(urlPrefix  + encounterAPI));

        return encounterJSON;
    }

    public void postResultEncounter(ResultEncounter resultEncounter) throws IOException, AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        String resultObject = objectMapper.writeValueAsString(resultEncounter);

        CloseableHttpClient client = HttpClients.createDefault();

        String urlPrefix = getURLPrefix();
        HttpPost httpPost = new HttpPost(URI.create(urlPrefix + "/openmrs/ws/rest/v1/encounter"));

        StringEntity entity = new StringEntity(resultObject);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        UsernamePasswordCredentials credUser = new UsernamePasswordCredentials("Superman", "Admin123");
        httpPost.addHeader(new BasicScheme().authenticate(credUser, httpPost, null));

        client.execute(httpPost);
        
        client.close();
    }
}
