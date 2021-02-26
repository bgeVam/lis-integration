package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import junit.framework.Assert;
import org.bahmni.module.lisintegration.atomfeed.builders.OpenMRSConceptBuilder;
import org.bahmni.module.lisintegration.atomfeed.builders.OpenMRSOrderBuilder;
import org.bahmni.module.lisintegration.atomfeed.client.Constants;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.exception.HL7MessageException;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HL7ServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Test
    public void testGenerateMessageControlIDShouldBeLessThan20Characters() throws Exception {
        HL7Service hl7Service = new HL7Service();
        String messageControlID = hl7Service.generateMessageControlID("ORD-35");

        Assert.assertTrue("HL7 Message control id should be less than 20 characters", messageControlID.length() <= 20);
    }

    @Test
    public void testGenerateMessageControlIDShouldBeLessThan20CharactersForLongOrderNumbers() throws Exception {
        HL7Service hl7Service = new HL7Service();
        String messageControlID = hl7Service.generateMessageControlID("ORD-3550000");

        Assert.assertTrue("HL7 Message control id should be less than 20 characters", messageControlID.length() <= 20);
    }

    @Test(expected = HL7MessageException.class)
    public void testShouldThrowExceptionWhenThereIsNoLISConceptSource() throws DataTypeException {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource("some source", "123", "LabSet")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        HL7Service hl7Service = new HL7Service();
        hl7Service.createMessage(order, patient, providers);
    }

    @Test
    public void testShouldCreateHL7Message() throws DataTypeException {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111").withConcept(buildConceptWithSource(Constants.LIS_CONCEPT_SOURCE_NAME, "123", "LabTest")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        HL7Service hl7Service = new HL7Service();
        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        Assert.assertNotNull(hl7Message);
        assertEquals("NW", hl7Message.getORDER().getORC().getOrderControl().getValue());
    }

    @Test
    public void testShouldCreateCancelOrderMessageForDiscontinuedOrder() throws Exception {
        initMocks(this);
                Order previousOrder = new Order(111, null, "someOrderUuid", "someTestName", "someTestPanel", "someTestUuid", null, "ORD-111", "Comment");
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-222").withConcept(buildConceptWithSource(Constants.LIS_CONCEPT_SOURCE_NAME, "123", " LabTest")).withPreviousOrderUuid(previousOrder.getOrderUuid()).withDiscontinued().build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();
        when(orderRepository.findByOrderUuid(order.getPreviousOrderUuid())).thenReturn(previousOrder);

        HL7Service hl7Service = new HL7Service(orderRepository);
        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, patient, providers);

        Assert.assertNotNull(hl7Message);
        assertEquals("CA", hl7Message.getORDER().getORC().getOrderControl().getValue());
        assertEquals("ORD-111", hl7Message.getORDER().getORC().getFillerOrderNumber().getEntityIdentifier().getValue());
    }

    @Test(expected = HL7MessageException.class)
    public void testShouldThrowExceptionForOrderNumberWithSizeExceedingLimit() throws Exception {

        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-11189067898900").withConcept(buildConceptWithSource(Constants.LIS_CONCEPT_SOURCE_NAME, "123", "LabTest")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        List<OpenMRSProvider> providers = getProvidersData();

        HL7Service hl7Service = new HL7Service();
        hl7Service.createMessage(order, patient, providers);
    }


    private OpenMRSConcept buildConceptWithSource(String conceptClassName, String lisCode, String conceptClass) {
        final OpenMRSConceptMapping mapping = new OpenMRSConceptMapping();
        mapping.setCode(lisCode);
        mapping.setName(lisCode);
        mapping.setSource(conceptClassName);
        mapping.setConceptClass(conceptClass);
        return new OpenMRSConceptBuilder().addConceptMapping(mapping).addConceptName(lisCode).addConceptClass(conceptClass).build();
    }

    private List<OpenMRSProvider> getProvidersData() {
        List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();
        providers.add(new OpenMRSProvider());
        return providers;
    }
}