package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.parser.PipeParser;
import junit.framework.Assert;
import org.bahmni.module.lisintegration.atomfeed.builders.OpenMRSConceptBuilder;
import org.bahmni.module.lisintegration.atomfeed.builders.OpenMRSOrderBuilder;
import org.bahmni.module.lisintegration.atomfeed.client.Constants;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.*;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.exception.HL7MessageException;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.bahmni.webclients.WebClientsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
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

    @Test(expected = WebClientsException.class)
    public void testShouldThrowExceptionWhenThereIsNoLISConceptSource() throws HL7Exception, IOException {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111")
                .withConcept(buildConceptWithSource("some source", "123", "LabSet")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        Sample sample = new Sample();
        List<OpenMRSProvider> providers = getProvidersData();
        List<Diagnosis> diagnosis = new ArrayList<Diagnosis>(0);

        HL7Service hl7Service = new HL7Service();
        hl7Service.createMessage(order, diagnosis, sample, patient, providers);
    }

    @Test(expected = WebClientsException.class)
    public void testShouldCreateHL7Message() throws HL7Exception, IOException {
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-111")
                .withConcept(buildConceptWithSource(Constants.LIS_CONCEPT_SOURCE_NAME, "123", "LabTest")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        Sample sample = new Sample();
        List<OpenMRSProvider> providers = getProvidersData();
        List<Diagnosis> diagnosis = new ArrayList<Diagnosis>(0);

        HL7Service hl7Service = new HL7Service();
        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, diagnosis, sample, patient, providers);

        Assert.assertNotNull(hl7Message);
        assertEquals("NW", hl7Message.getORDER().getORC().getOrderControl().getValue());
    }

    @Test(expected = WebClientsException.class)
    public void testShouldCreateCancelOrderMessageForDiscontinuedOrder() throws Exception {
        initMocks(this);
        Order previousOrder = new Order(111, null, "somePlacerOrderUuid", "someTestName", "someTestPanel", "someTestUuid",
                null, "ORD-111", "Comment", "someOrderFillerUuid");
        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-222")
                .withConcept(buildConceptWithSource(Constants.LIS_CONCEPT_SOURCE_NAME, "123", " LabTest"))
                .withPreviousOrderUuid(previousOrder.getPlacerOrderUuid()).withDiscontinued().build();
        OpenMRSPatient patient = new OpenMRSPatient();
        Sample sample = new Sample();
        List<OpenMRSProvider> providers = getProvidersData();
        List<Diagnosis> diagnosis = new ArrayList<Diagnosis>(0);
        when(orderRepository.findByPlacerOrderUuid(order.getPreviousOrderUuid())).thenReturn(previousOrder);

        HL7Service hl7Service = new HL7Service(orderRepository);
        ORM_O01 hl7Message = (ORM_O01) hl7Service.createMessage(order, diagnosis, sample, patient, providers);

        Assert.assertNotNull(hl7Message);
        assertEquals("CA", hl7Message.getORDER().getORC().getOrderControl().getValue());
        assertEquals("somePlacerOrderUuid",
                hl7Message.getORDER().getORC().getPlacerOrderNumber().getEntityIdentifier().getValue());
        assertEquals("someOrderFillerUuid",
                hl7Message.getORDER().getORC().getFillerOrderNumber().getEntityIdentifier().getValue());
    }

    @Test(expected = WebClientsException.class)
    public void testShouldThrowExceptionForOrderNumberWithSizeExceedingLimit() throws Exception {

        OpenMRSOrder order = new OpenMRSOrderBuilder().withOrderNumber("ORD-11189067898900")
                .withConcept(buildConceptWithSource(Constants.LIS_CONCEPT_SOURCE_NAME, "123", "LabTest")).build();
        OpenMRSPatient patient = new OpenMRSPatient();
        Sample sample = new Sample();
        List<OpenMRSProvider> providers = getProvidersData();
        List<Diagnosis> diagnosis = new ArrayList<Diagnosis>(0);

        HL7Service hl7Service = new HL7Service();
        hl7Service.createMessage(order, diagnosis, sample, patient, providers);
    }

    @Test
    public void testTheDiagnosis() throws HL7Exception, IOException, ParseException {
        Date diagnosisDate = new Date();
        HL7Service hl7Service = new HL7Service();
        ORM_O01 orm = new ORM_O01();

        Diagnosis dg1_1 = new Diagnosis();
        dg1_1.setName("name");
        dg1_1.setCode("code");
        dg1_1.setType("type");
        dg1_1.setDate(diagnosisDate);
        hl7Service.addDiagnosis(orm, dg1_1, 0);

        MSH msh = orm.getMSH();
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getProcessingID().getProcessingID().setValue("P");
        msh.getVersionID().getVersionID().setValue("2.5");

        String DG1Code = orm.getORDER().getORDER_DETAIL().getDG1(0).getDiagnosisCodeDG1().getIdentifier().getValue();
        String DG1Name = orm.getORDER().getORDER_DETAIL().getDG1(0).getDiagnosisCodeDG1().getText().getValue();
        String DG1Type = orm.getORDER().getORDER_DETAIL().getDG1(0).getDiagnosisType().getValue();

        assertEquals("code", DG1Code);
        assertEquals("name", DG1Name);
        assertEquals("type", DG1Type);
    }

    private OpenMRSConcept buildConceptWithSource(String conceptClassName, String lisCode, String conceptClass) {
        final OpenMRSConceptMapping mapping = new OpenMRSConceptMapping();
        mapping.setCode(lisCode);
        mapping.setName(lisCode);
        mapping.setSource(conceptClassName);
        mapping.setConceptClass(conceptClass);
        return new OpenMRSConceptBuilder().addConceptMapping(mapping).addConceptName(lisCode)
                .addConceptClass(conceptClass).build();
    }

    private List<OpenMRSProvider> getProvidersData() {
        List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();
        providers.add(new OpenMRSProvider());
        return providers;
    }
}
