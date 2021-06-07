package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.v25.message.ADR_A19;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Diagnosis;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSVisit;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.model.OrderDetails;
import org.bahmni.module.lisintegration.model.OrderType;
import org.bahmni.module.lisintegration.repository.OrderDetailsRepository;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.bahmni.module.lisintegration.repository.OrderTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class LisIntegrationServiceTest {
    @Mock
    OrderTypeRepository orderTypeRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderDetailsRepository orderDetailsRepository;

    @Mock
    OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper;

    @InjectMocks
    LisIntegrationService lisIntegrationService = new LisIntegrationService();

    @Mock
    private OpenMRSService openMRSService;

    @Mock
    private HL7Service hl7Service;

    @Mock
    private OpenMRSConcept openMRSConcept;

    @Mock
    ADR_A19 adr_a19;

    @Mock
    private LisService lisService;

    private String PATIENT_UUID = "patient1";
    private String FILLER_ORDER_UUID = "fillerOrderUuid1";

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldProcessAnEncounterWithTwoOrders() throws LLPException, HL7Exception, ParseException, IOException {
        OpenMRSEncounter encounter = buildEncounter();
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByPlacerOrderUuid(any(String.class))).thenReturn(null);
        when(hl7Service.createMessage(any(OpenMRSOrder.class), anyListOf(Diagnosis.class), any(Sample.class), any(OpenMRSPatient.class),
                any(OpenMRSVisit.class), any(List.class))).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(openMRSConcept.getUuid()).thenReturn("f6879abe-ac34-4b35-ae87-2b1c84f9a0fb");
        when(openMRSService.getSample("f6879abe-ac34-4b35-ae87-2b1c84f9a0fb")).thenReturn(new Sample());

        lisIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderDetailsRepository, times(2)).save(any(OrderDetails.class));
    }

    @Test
    public void shouldNotProcessAlreadyProcessedOrder() throws IOException, ParseException, LLPException, HL7Exception {
        OpenMRSEncounter encounter = buildEncounter();
        when(openMRSService.getPatient(PATIENT_UUID)).thenReturn(new OpenMRSPatient());
        when(orderTypeRepository.findAll()).thenReturn(getAcceptableOrderTypes());
        when(orderRepository.findByPlacerOrderUuid(any(String.class))).thenReturn(null).thenReturn(new Order());
        when(hl7Service.createMessage(any(OpenMRSOrder.class), anyListOf(Diagnosis.class), any(Sample.class), any(OpenMRSPatient.class),
               any(OpenMRSVisit.class), any(List.class))).thenReturn(adr_a19);
        when(adr_a19.encode()).thenReturn("Request message");
        when(openMRSConcept.getUuid()).thenReturn("f6879abe-ac34-4b35-ae87-2b1c84f9a0fb");
        when(openMRSService.getSample("f6879abe-ac34-4b35-ae87-2b1c84f9a0fb")).thenReturn(new Sample());

        lisIntegrationService.processEncounter(encounter);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderDetailsRepository, times(1)).save(any(OrderDetails.class));
    }

    OpenMRSEncounter buildEncounter() {
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setPatientUuid(PATIENT_UUID);
        OpenMRSConcept concept = new OpenMRSConcept();
        concept.setUuid("f5774c43-1e4c-46fa-a06a-4e2c684b154c");
        OpenMRSOrder order1 = new OpenMRSOrder("uuid1", "type1", concept, false, null, null, FILLER_ORDER_UUID);
        OpenMRSOrder order2 = new OpenMRSOrder("uuid2", "type2", concept, false, null, null, FILLER_ORDER_UUID);
        openMRSEncounter.setOrders(Arrays.asList(order1, order2));
        return openMRSEncounter;
    }

    List<OrderType> getAcceptableOrderTypes() {
        ArrayList<OrderType> acceptableOrderTypes = new ArrayList<OrderType>();
        acceptableOrderTypes.add(new OrderType(1, "type1", null));
        acceptableOrderTypes.add(new OrderType(2, "type2", null));
        return acceptableOrderTypes;
    }

}