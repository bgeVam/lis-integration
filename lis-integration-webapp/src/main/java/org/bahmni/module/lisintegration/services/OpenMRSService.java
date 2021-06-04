package org.bahmni.module.lisintegration.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bahmni.module.lisintegration.atomfeed.client.ConnectionDetails;
import org.bahmni.module.lisintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Diagnosis;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSPerson;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.ResultEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.UploadDocument;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.atomfeed.mappers.DiagnosisMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSEncounterMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSPatientMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSPersonMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.OrderMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.SampleMapper;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import org.apache.log4j.Logger;

@Component
public class OpenMRSService {
    private String patientRestUrl = "/openmrs/ws/rest/v1/patient/";
    private String personRestUrl = "/openmrs/ws/rest/v1/person/";
    private String providerRestUrl = "/openmrs/ws/rest/v1/provider/";

    private static final org.apache.log4j.Logger LOG = Logger.getLogger(OpenMRSService.class);

    @Value("${green.letters}")
    private String printGreen;

    @Value("${red.letters}")
    private String printRed;

    @Value("${default.letters}")
    private String printDefault;

    @Autowired
    private DiagnosisMapper diagnosisMapper;

    public OpenMRSEncounter getEncounter(String encounterUrl) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String encounterJSON = webClient.get(URI.create(urlPrefix + encounterUrl));
        return new OpenMRSEncounterMapper(ObjectMapperRepository.objectMapper).map(encounterJSON);
    }

    public OpenMRSPatient getPatient(String patientUuid) throws IOException, ParseException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String patientJSON = webClient.get(URI.create(urlPrefix + patientRestUrl + patientUuid + "?v=full"));
        return new OpenMRSPatientMapper().map(patientJSON);
    }

    public Sample getSample(String conceptUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String labSamplesConceptAPI = "/openmrs/ws/rest/v1/concept/8160a011-3f10-11e4-adec-0800271c1b75?v=full";

        String labSamplesConcept = webClient.get(URI.create(urlPrefix + labSamplesConceptAPI));
        ObjectMapper labSamplesObjectMapper = ObjectMapperRepository.objectMapper;
        JsonNode labSamplesJSON = labSamplesObjectMapper.readTree(labSamplesConcept);
        JsonNode samples = labSamplesJSON.path("setMembers");
        JsonNode sampleJSON = null;

        for (JsonNode sample : samples) {
            JsonNode sampleMembers = sample.path("setMembers");
            for (JsonNode sampleMember : sampleMembers) {
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

        String orderAPI = "/openmrs/ws/rest/v1/order/" + orderUUID;
        String orderJSON = webClient.get(URI.create(urlPrefix + orderAPI));

        return new OrderMapper().map(orderJSON);
    }

    public String getEncounterByUUID(String encounterUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String encounterAPI = "/openmrs/ws/rest/v1/encounter/" + encounterUUID + "?v=full";
        String encounterJSON = webClient.get(URI.create(urlPrefix + encounterAPI));

        return encounterJSON;
    }

    /**
     * postResult is the method which is called for posting results of order and uploading document.
     * Please see the {@link PostResult} for the interface implemented in three classes {@link ResultEncounter},
     * {@link UploadDocument} and {@link VisitDocument}
     * @param postResult used to determine how the url needs to be constructed
     * @return responseString returns results from API
     * @throws IOException
     * @throws ClientProtocolException
     * @throws AuthenticationException
     */
    public String postResult(PostResult postResult)
            throws IOException, ClientProtocolException, AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        String resultObject = objectMapper.writeValueAsString(postResult);
        String responseString = new String();
        CloseableHttpClient client = HttpClients.createDefault();

        String urlPrefix = getURLPrefix();
        String postURL = postResult.getPostUrl(urlPrefix);
        URI uri = URI.create(postURL);
        HttpPost httpPost = new HttpPost(uri.toString());

        fillHttpPostRequest(resultObject, httpPost);
        HttpResponse httpResponse = client.execute(httpPost);
        HttpEntity responseEntity = httpResponse.getEntity();
        Integer statusCode = httpResponse.getStatusLine().getStatusCode();
        if (responseEntity != null) {
            responseString = EntityUtils.toString(responseEntity);
            if (postResult instanceof ResultEncounter) {
                if (statusCode >= 200 && statusCode < 300) {
                    LOG.debug(printGreen + " Result was posted successfully!" + printDefault);
                    LOG.debug("HTTP Response Object: " + httpResponse);
                    LOG.debug("HTTP Response Body: " + responseString);
                } else {
                    LOG.error(printRed + "Result was posted unsuccessfully!" + printDefault);
                    LOG.error("HTTP Response Object: " + httpResponse);
                    LOG.error("HTTP Response Body: " + responseString);
                }
            } else if (postResult instanceof UploadDocument) {
                if (statusCode >= 200 && statusCode < 300) {
                    LOG.debug(printGreen + "Document was posted successfully!" + printDefault);
                    LOG.debug("HTTP Response Object: " + httpResponse);
                    LOG.debug("HTTP Response Body: " + responseString);
                } else {
                    LOG.error(printRed + "Document was posted unsuccessfully!" + printDefault);
                    LOG.error("HTTP Response Object: " + httpResponse);
                    LOG.error("HTTP Response Body: " + responseString);
                }
            }
        } else {
            LOG.error(printRed + "Nothing received - responseEntity is null." + printDefault);
        }
        client.close();
        return responseString;
    }

    public String getPatientDocumentTypeUuid(String patinetDocumentType) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String concpetPatientDocumentAPI = "/openmrs/ws/rest/v1/concept/c46896fd-3f10-11e4-adec-0800271c1b75?v=full";
        String patientDocumentConcpet = webClient.get(URI.create(urlPrefix + concpetPatientDocumentAPI));

        ObjectMapper concpetPatientDocumentMapper = ObjectMapperRepository.objectMapper;
        JsonNode patientDocumentJSON = concpetPatientDocumentMapper.readTree(patientDocumentConcpet);
        JsonNode documentTypes = patientDocumentJSON.path("setMembers");
        String documentTypeJSON = null;

        for (JsonNode documentType : documentTypes) {
            if (patinetDocumentType.equals(documentType.path("name").path("name").asText())) {
                documentTypeJSON = documentType.path("uuid").getTextValue();
                break;
            }
        }
        return documentTypeJSON;
    }

    void fillHttpPostRequest(String resultObject, HttpPost httpPost)
            throws UnsupportedEncodingException, AuthenticationException {
        StringEntity entity = new StringEntity(resultObject);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        UsernamePasswordCredentials credUser = new UsernamePasswordCredentials("Superman", "Admin123");
        httpPost.addHeader(new BasicScheme().authenticate(credUser, httpPost, null));
    }

    public List<OpenMRSConcept> getTestsOfPanel(String conceptPanelUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String conceptPanelAPI = "/openmrs/ws/rest/v1/concept/" + conceptPanelUUID + "?v=full";
        String conceptPanelContent = webClient.get(URI.create(urlPrefix + conceptPanelAPI));

        ObjectMapper testsObjectMapper = ObjectMapperRepository.objectMapper;
        JsonNode conceptPanelJSON = testsObjectMapper.readTree(conceptPanelContent);
        JsonNode testsOfPanel = conceptPanelJSON.path("setMembers");
        List<OpenMRSConcept> openMRSConceptsList = new ArrayList<OpenMRSConcept>(testsOfPanel.size());

        for (JsonNode test : testsOfPanel) {
            OpenMRSConcept testConcept = new OpenMRSConcept();
            OpenMRSConceptName openMRSConceptName = new OpenMRSConceptName();
            openMRSConceptName.setName(test.path("name").path("name").asText());

            testConcept.setName(openMRSConceptName);
            testConcept.setUuid(test.path("uuid").asText());
            openMRSConceptsList.add(testConcept);
        }
        return openMRSConceptsList;
    }

    /**
     * getDiagnosis is the method which is called for fetching the list of diagnosis of one patient.
     *
     * @param encounterUUID used to fetch the encounter data.
     * @return list of diagnosis fetched by the encounter.
     * @throws IOException
     * @throws ParseException
     */
    public List<Diagnosis> getDiagnosis(String encounterUUID) throws IOException, ParseException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();

        String encounterAPI = "/openmrs/ws/rest/v1/encounter/" + encounterUUID + "?v=full";
        String encounterJSON = webClient.get(URI.create(urlPrefix + encounterAPI));

        return diagnosisMapper.map(encounterJSON);
    }

     /**
     * getDiagnosisCode is the method that is called to fetch the code of one diagnosis.
     *
     * @param diagnosisUuid used to fetch the diagnosis details
     * @return code of diagnosis
     * @throws IOException
     */
    public String getDiagnosisCode(String diagnosisUuid) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String diagnosisCodeAPI = "/openmrs/ws/rest/v1/conceptreferenceterm/" + diagnosisUuid + "?v=full";

        String diagnosisReferenceJSON = webClient.get(URI.create(urlPrefix + diagnosisCodeAPI));
        ObjectMapper diagnosisCodetMapper = ObjectMapperRepository.objectMapper;
        JsonNode diagnosisCodeJson = diagnosisCodetMapper.readTree(diagnosisReferenceJSON);
        String code = diagnosisCodeJson.path("code").asText();
        return code;
    }

    /**
     * getConcept is the method which is called to fetch all the data of one concept.
     *
     * @param conceptUuid used to fetch the concept details
     * @return all data of concept
     * @throws IOException
     */
    public String getConcept(String conceptUuid) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String concpetAPI = "/openmrs/ws/rest/v1/concept/" + conceptUuid + "?v=full";

        String concpet = webClient.get(URI.create(urlPrefix + concpetAPI));
        return concpet;
    }
    /**
     * The method getPersonUuidByProviderUuid is called to fetch person uuid from provider uuid
     *
     * @param providerUUID used to fetch provider details
     * @return String - person uuid
     * @throws IOException if the message cannot be created via
     *                     {@link #readTree(providerJson)}
     */
    public String getPersonUuidByProviderUuid(String providerUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String providerJson =  webClient.get(URI.create(urlPrefix + providerRestUrl + providerUUID + "?v=full"));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode json = objectMapper.readTree(providerJson);
        String personUUID = json.path("person").path("uuid").asText();

        return personUUID;
    }

    /**
     * The method getPerson is called to fetch person data from person uuid
     *
     * @param personUUID used to fetch person details
     * @return personJSON mapped
     * @throws IOException if the message cannot be created via
     *                     {@link #map(personJSON)}
     *                     method
     */
    public OpenMRSPerson getPerson(String personUUID) throws IOException {
        HttpClient webClient = WebClientFactory.getClient();
        String urlPrefix = getURLPrefix();
        String personJSON = webClient.get(URI.create(urlPrefix + personRestUrl + personUUID + "?v=full"));

        return new OpenMRSPersonMapper().map(personJSON);
    }

}
