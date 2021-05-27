package org.bahmni.module.lisintegration.atomfeed.mappers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Arrays;

import org.bahmni.module.lisintegration.atomfeed.OpenMRSMapperBaseTest;
import org.bahmni.module.lisintegration.atomfeed.client.WebClientFactory;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSObs;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSVisit;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.ResultEncounter;
import org.bahmni.module.lisintegration.services.OpenMRSService;
import org.bahmni.webclients.HttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import junit.framework.Assert;

@PrepareForTest(WebClientFactory.class)
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class ResultMapperTest extends OpenMRSMapperBaseTest {

    @Mock
    private HttpClient webClient;

    @Mock
    private org.bahmni.webclients.ConnectionDetails connectionDetails;
    
    @Test
    public void testMap() throws Exception {

        PowerMockito.mockStatic(WebClientFactory.class);
        when(WebClientFactory.getClient()).thenReturn(webClient);
        when(webClient.get(new URI("http://localhost:8050/encounter/1"))).thenReturn(new OpenMRSMapperBaseTest().deserialize("/sampleOpenMRSEncounter.json"));

        when(webClient.get(any(URI.class))).thenReturn(deserialize("/sampleOpenMRSEncounter.json"));
        when(connectionDetails.getAuthUrl()).thenReturn("urlPrefix");

        OpenMRSEncounter encounter = new OpenMRSService().getEncounter("/encounter/1");
        OpenMRSObs openMRSObs = new OpenMRSObs();

        OpenMRSConcept concept = new OpenMRSConcept();
        concept.setUuid("uuid-test-uuid-test-concpet");
        OpenMRSOrder order = new OpenMRSOrder();
        order.setUuid("uuid-test-uuid-test-order");
        OpenMRSVisit visit = new OpenMRSVisit();
        visit.setUuid("uuid-test-uuid-test-visit");

        OpenMRSObs valueGroupMember = new OpenMRSObs();
        valueGroupMember.setConcept(concept);
        valueGroupMember.setValue(5.0);
        openMRSObs.setConcept(concept);
        openMRSObs.setObsDateTime("obsDateTime");
        openMRSObs.setOrder(order);
        openMRSObs.setGroupMembers(Arrays.asList(valueGroupMember));
        encounter.setVisit(visit);
        encounter.setObs(Arrays.asList(openMRSObs));
        ResultEncounter result = new ResultMapper().map(encounter);

        Assert.assertEquals("105059a8-5226-4b1f-b512-0d3ae685287d", result.getPatient());
        Assert.assertEquals("uuid-test-uuid-test-order", result.getObs().get(0).getOrder());
        Assert.assertEquals(5.0, result.getObs().get(0).getGroupMembers().get(0).getValue());
    }
}
