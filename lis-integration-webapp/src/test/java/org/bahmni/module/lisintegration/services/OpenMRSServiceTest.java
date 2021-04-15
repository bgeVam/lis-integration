package org.bahmni.module.lisintegration.services;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.bahmni.module.lisintegration.atomfeed.*;
import org.bahmni.module.lisintegration.atomfeed.client.*;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.*;
import org.bahmni.webclients.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.*;
import org.junit.Test;
import org.junit.runner.*;
import org.mockito.Mock;
import org.powermock.api.mockito.*;
import org.powermock.core.classloader.annotations.*;
import org.powermock.modules.junit4.*;

import java.net.*;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

@PrepareForTest(WebClientFactory.class)
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class OpenMRSServiceTest extends OpenMRSMapperBaseTest {

    @Mock
    private HttpClient webClient;

    @Mock
    private HttpPost httpPost;

    @Mock
    private org.bahmni.webclients.ConnectionDetails connectionDetails;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetEncounter() throws Exception{
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8050/encounter/1"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleOpenMRSEncounter.json"));

        when(webClient.get(any(URI.class))).thenReturn(deserialize("/sampleOpenMRSEncounter.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        OpenMRSEncounter encounter = new OpenMRSService().getEncounter("/encounter/1");

        assertEquals("7820b07d-50e9-4fed-b991-c38692b3d4ec", encounter.getEncounterUuid());
    }

    @Test
    public void shouldGetPatient() throws Exception {
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        String patientUuid = "105059a8-5226-4b1f-b512-0d3ae685287d";
        String identifier = "GAN200053";
        when(webClient.get(new URI("http://localhost:8050/openmrs/ws/rest/v1/patient/" + patientUuid+"?v=full"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/samplePatient.json"));

        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        OpenMRSPatient patient = new OpenMRSService().getPatient(patientUuid);

        assertEquals(identifier, patient.getPatientId());

    }

    @Test
    public void shouldGetSample() throws Exception{
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8050/openmrs/ws/rest/v1/concept/8160a011-3f10-11e4-adec-0800271c1b75?v=full"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleLabSamplesConcept.json"));

        when(webClient.get(any(URI.class))).thenReturn(deserialize("/sampleLabSamplesConcept.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        Sample sample = new OpenMRSService().getSample("f5774c43-1e4c-46fa-a06a-4e2c684b154c");

        assertEquals("Blood", sample.getName());
    }

    @Test
    public void shouldGetEncounterByUuid() throws Exception{
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8050/openmrs/ws/rest/v1/encounter/45efbd4e-1ce6-4c60-8b40-af3543100e11?v=full"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/encounter.json"));

        when(webClient.get(any(URI.class))).thenReturn(deserialize("/encounter.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        String encounterJSON = new OpenMRSService().getEncounterByUUID("45efbd4e-1ce6-4c60-8b40-af3543100e11");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode encounterJSONNode = objectMapper.readTree(encounterJSON);
        
        assertEquals("45efbd4e-1ce6-4c60-8b40-af3543100e11", encounterJSONNode.path("uuid").getTextValue());
    }

    @Test
    public void shouldGetOrder() throws Exception{
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8050/openmrs/ws/rest/v1/order/5330e5d0-f134-45d4-8615-f5462492481e?v=full"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/order.json"));

        when(webClient.get(any(URI.class))).thenReturn(deserialize("/order.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        OpenMRSOrder order = new OpenMRSService().getOrder("5330e5d0-f134-45d4-8615-f5462492481e");
        
        assertEquals("5330e5d0-f134-45d4-8615-f5462492481e", order.getUuid());
    }

    @Test
    public void fillHttpPostRequestTest() throws Exception{

        httpPost = new HttpPost(new URI("http://localhost:8051"));
        OpenMRSService service = new OpenMRSService();
        service.fillHttpPostRequest("{\"name\": \"vsk\"}", httpPost);
        Header[] contentType = httpPost.getHeaders("Content-type");

        assertNotNull(httpPost);
        assertEquals("POST",  httpPost.getMethod());
        assertEquals("application/json",  contentType[0].getValue());
        assertEquals("http://localhost:8051",  httpPost.getURI().toString());
        assertEquals("{\"name\": \"vsk\"}", EntityUtils.toString(httpPost.getEntity()));
    }

    @Test
    public void shouldGetTestsOfPanel() throws Exception {
        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI(
                "http://localhost:8050/openmrs/ws/rest/v1/concept/fc86f477-f040-4ce1-aa70-1697e6831752?v=full")))
                        .thenReturn(new OpenMRSMapperBaseTest().deserialize("/conceptPanel.json"));

        when(webClient.get(any(URI.class))).thenReturn(deserialize("/conceptPanel.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        List<OpenMRSConcept> testConcepts = new OpenMRSService()
                .getTestsOfPanel("fc86f477-f040-4ce1-aa70-1697e6831752?v=full");

        assertEquals("Leukocytes", testConcepts.get(0).getName().getName());
        assertEquals("4e905b9d-83f6-43c6-b388-0e1f9490c39b", testConcepts.get(0).getUuid());
    }
}
