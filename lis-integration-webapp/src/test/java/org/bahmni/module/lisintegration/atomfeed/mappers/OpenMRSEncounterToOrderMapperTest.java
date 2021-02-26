package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.builders.*;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.model.OrderType;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OpenMRSEncounterToOrderMapperTest {
    @Mock
    private OrderRepository orderRepository;

    OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper = new OpenMRSEncounterToOrderMapper();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldMapOnlyLabOpenMRSOrdersToOrders() throws Exception {
        String testName = "MCV";
        String testPanelName = "LabTest";
        String orderNumber = "ORD-001";
        String testUuid = "concept uuid";
        String orderUuid = "lab order uuid";
        String providerName = "Albion Shala";
        OpenMRSConceptName conceptName = new OpenMRSConceptNameBuilder().withName(testName).build();
        OpenMRSConcept concept = new OpenMRSConceptBuilder().withUuid(testUuid).withName(conceptName).addConceptClass(testPanelName).build();
        OpenMRSOrder labOrder = new OpenMRSOrderBuilder().withOrderUuid(orderUuid).withOrderType("Lab Order").withOrderNumber(orderNumber).withVoided(false).withConcept(concept).build();
        OpenMRSOrder radiologyOrder = new OpenMRSOrderBuilder().withOrderUuid("radiology order uuid").withOrderType("Radiology Order").withVoided(false).withConcept(concept).build();
        OpenMRSProvider openMRSProvider = new OpenMRSProvider("provider-uuid", providerName);
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounterBuilder().withEncounterUuid("encounter uuid").withPatientUuid("patient uuid")
                .withTestOrder(radiologyOrder).withTestOrder(labOrder).withProvider(openMRSProvider).build();

        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderTypeBuilder().withName("Lab Order").build());

        when(orderRepository.findByOrderUuid(orderUuid)).thenReturn(null);

        Order order = openMRSEncounterToOrderMapper.map(labOrder, openMRSEncounter, acceptableOrderTypes);

        assertNotNull(order);
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(orderUuid, order.getOrderUuid());
        assertEquals(testName, order.getTestName());
        assertEquals(testUuid, order.getTestUuid());
        assertEquals(providerName, order.getCreator());
    }




}