package org.bahmni.module.lisintegration.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.net.URI;
import java.util.List;

import org.bahmni.module.lisintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.lisintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.lisintegration.atomfeed.worker.EncounterFeedWorker;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.bahmni.webclients.HttpClient;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import junit.framework.Assert;

@SqlGroup({ @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:insertModalities.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:insertOrderTypes.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:truncateTables.sql")

})
@PrepareForTest(WebClientFactory.class)
public class EncounterFeedWorkerIT extends BaseIntegrationTest {
    @Mock
    private HttpClient webClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EncounterFeedWorker encounterFeedWorker;

    @Before
    public void setUp() throws Exception {
        mockStatic(WebClientFactory.class);
        initMocks(this);

        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8050/encounter/1")))
                .thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleOpenMRSEncounter.json"));
        when(webClient.get(new URI(
                "http://localhost:8050/openmrs/ws/rest/v1/patient/105059a8-5226-4b1f-b512-0d3ae685287d?v=full")))
                        .thenReturn(new OpenMRSMapperBaseTest().deserialize("/samplePatient.json"));
        when(webClient.get(new URI(
                "http://localhost:8050/openmrs/ws/rest/v1/concept/8160a011-3f10-11e4-adec-0800271c1b75?v=full")))
                        .thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleLabSamplesConcept.json"));
    }

    @Test
    public void shouldSendRadiologyOrderToALis() throws Exception {
        encounterFeedWorker.process(new Event("event id", "/encounter/1"));

        List<Order> savedOrders = orderRepository.findAll();
        assertEquals(1, savedOrders.size());
        assertEquals("08d2dfa2-2274-44a1-a29e-30ea02df2798", savedOrders.get(0).getPlacerOrderUuid());
        assertEquals("HEAD Skull AP", savedOrders.get(0).getTestName());
        assertEquals("c42e71d7-3f10-11e4-adec-0800271c1b75", savedOrders.get(0).getTestUuid());

    }

    @Test
    public void shouldAddToFailedEventsWhenLisIsNotAvailable() throws Exception {
        lisStubServer.stopAndWait();
        try {
            encounterFeedWorker.process(new Event("event id", "/encounter/1"));
            Assert.fail("Should be throwing a exception since lis is down");
        } catch (RuntimeException e) {
            Assert.assertEquals("Failed send order to lis", e.getMessage());
            List<Order> savedOrders = orderRepository.findAll();
            assertEquals(0, savedOrders.size());
        }
    }

}
