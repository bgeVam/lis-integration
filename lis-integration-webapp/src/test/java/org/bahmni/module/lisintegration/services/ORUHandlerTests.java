package org.bahmni.module.lisintegration.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSObs;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.PatientDocument;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.UploadDocument;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.VisitDocument;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.atomfeed.mappers.OrderMapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class ORUHandlerTests {

    @Test
    public void testVisitDocument() throws JsonProcessingException, IOException {
        String encounterPatientDocumentUUID = "UUID-TEST-UUID-TEST";
        String patientDocumentTypeUUID = "UUID-TEST-UUID-TEST";
        String image = "TestDocument.pdf";
        String orderEncounter = "{ \"uuid\":\"08b5e000-68bb-4474-9197-45eeeb32b908\", \"display\":\"Consultation 03/29/2021\", \"encounterDatetime\":\"2021-03-29T15:25:29.000+0000\", "
                                + "\"location\":{ \"uuid\":\"96c7493b-fac2-45bf-b319-f47ae78bc839\", \"display\":\"COV19 Ward\", \"name\":\"COV19 Ward\", \"description\":\"Ward D3 part Main Hospital in Somewhere\" }, "
                                + "\"form\":null, \"encounterType\":{ \"uuid\":\"81852aee-3f10-11e4-adec-0800271c1b75\", \"display\":\"Consultation\", \"name\":\"Consultation\", "
                                + "\"description\":\"Consultation encounter\" }, \"orders\":[ { \"uuid\":\"c4e49697-844c-4b7e-8879-3aa26c434fa0\", \"orderNumber\":\"ORD-307\", \"accessionNumber\":null, "
                                + "\"patient\":{ \"uuid\":\"7edb7988-071b-40d7-8057-daa544b492a2\", \"display\":\"WWW445427 - asd asd asd\" } } ], \"visit\":{ \"uuid\":\"232e9e8b-157c-4ed4-a674-ce7d447bcf25\", "
                                + "\"display\":\"EMERGENCY @ Somewhere C1 - 03/29/2021 03:25 PM\", \"visitType\":{ \"uuid\":\"2693e2cc-3e37-4469-a73c-456615329923\", \"display\":\"EMERGENCY\" }, \"indication\":null, "
                                + "\"location\":{ \"uuid\":\"ffe6c4f3-3e83-472f-9444-5c4d18df9716\", \"display\":\"Somewhere C1\" }, \"startDatetime\":\"2021-03-29T15:25:08.000+0000\", \"stopDatetime\":null, "
                                + "\"encounters\":[ { \"uuid\":\"08b5e000-68bb-4474-9197-45eeeb32b908\", \"display\":\"Consultation 03/29/2021\" } ] }, \"encounterProviders\":[ { \"uuid\":\"2db62125-4c31-48e8-ac57-55729e3396f4\", " 
                                + "\"provider\":{ \"uuid\":\"c1c26908-3f10-11e4-adec-0800271c1b75\", \"display\":\"superman - Super Man\" }, \"encounterRole\":{ \"uuid\":\"a0b03050-c99b-11e0-9572-0800200c9a66\", \"display\":\"Unknown\" } } ] }";
        
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        OpenMRSPatient openMRSPatient = new OpenMRSPatient();
        openMRSPatient.setPatientUUID("UUID-TEST-UUID-TEST");
        OpenMRSOrder openMRSOrder = new OpenMRSOrder();
        openMRSOrder.setPatient(openMRSPatient);
        openMRSEncounter.setOrders(Arrays.asList(openMRSOrder));;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode encounterJSONNode = objectMapper.readTree(orderEncounter);

        VisitDocument visitDocument =  ORUHandler.generateVisitDocument(openMRSEncounter, encounterJSONNode, image, patientDocumentTypeUUID, encounterPatientDocumentUUID);

        assertEquals("UUID-TEST-UUID-TEST", visitDocument.getPatientUuid());
        assertEquals("96c7493b-fac2-45bf-b319-f47ae78bc839", visitDocument.getLocationUuid());
        assertEquals(patientDocumentTypeUUID, visitDocument.getEncounterTypeUuid());
        assertEquals("232e9e8b-157c-4ed4-a674-ce7d447bcf25", visitDocument.getVisitUuid());
    }

    @Test
    public void testGenerateUploadDocument() {
        String formatFilePDF = "pdf";
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        OpenMRSPatient openMRSPatient = new OpenMRSPatient();
        openMRSPatient.setPatientUUID("UUID-TEST-UUID-TEST");
        OpenMRSOrder openMRSOrder = new OpenMRSOrder();
        openMRSOrder.setPatient(openMRSPatient);
        PatientDocument patientDocument = new PatientDocument();
        patientDocument.setConctent("Content-of-pdf-document");
        patientDocument.setEncounterTypeName("pdf");
        openMRSEncounter.setOrders(Arrays.asList(openMRSOrder));
        openMRSEncounter.setPatientDocument(patientDocument);

        UploadDocument uploadDocument = ORUHandler.generateUploadDocument(openMRSEncounter, formatFilePDF);
        
        assertEquals("pdf", uploadDocument.getFormat());
        assertEquals("UUID-TEST-UUID-TEST", uploadDocument.getPatientUuid());
        assertEquals(formatFilePDF, uploadDocument.getEncounterTypeName());
    }

    @Test
    public void testFillOpenMRSEncounter() throws JsonProcessingException, IOException {
        String providerUUID = "UUID-TEST-UUID-TEST";
        String encounterLabResultUUID = "UUID-TEST-UUID-TEST";
        String encounterRoleUUID = "UUID-TEST-UUID-TEST";
        String orderEncounter = "{ \"uuid\":\"08b5e000-68bb-4474-9197-45eeeb32b908\", \"display\":\"Consultation 03/29/2021\", \"encounterDatetime\":\"2021-03-29T15:25:29.000+0000\", "
                                + "\"location\":{ \"uuid\":\"96c7493b-fac2-45bf-b319-f47ae78bc839\", \"display\":\"COV19 Ward\", \"name\":\"COV19 Ward\", \"description\":\"Ward D3 part Main Hospital in Somewhere\" }, "
                                + "\"form\":null, \"encounterType\":{ \"uuid\":\"81852aee-3f10-11e4-adec-0800271c1b75\", \"display\":\"Consultation\", \"name\":\"Consultation\", "
                                + "\"description\":\"Consultation encounter\" }, \"orders\":[ { \"uuid\":\"c4e49697-844c-4b7e-8879-3aa26c434fa0\", \"orderNumber\":\"ORD-307\", \"accessionNumber\":null, "
                                + "\"patient\":{ \"uuid\":\"7edb7988-071b-40d7-8057-daa544b492a2\", \"display\":\"WWW445427 - asd asd asd\" } } ], \"visit\":{ \"uuid\":\"232e9e8b-157c-4ed4-a674-ce7d447bcf25\", "
                                + "\"display\":\"EMERGENCY @ Somewhere C1 - 03/29/2021 03:25 PM\", \"visitType\":{ \"uuid\":\"2693e2cc-3e37-4469-a73c-456615329923\", \"display\":\"EMERGENCY\" }, \"indication\":null, "
                                + "\"location\":{ \"uuid\":\"ffe6c4f3-3e83-472f-9444-5c4d18df9716\", \"display\":\"Somewhere C1\" }, \"startDatetime\":\"2021-03-29T15:25:08.000+0000\", \"stopDatetime\":null, "
                                + "\"encounters\":[ { \"uuid\":\"08b5e000-68bb-4474-9197-45eeeb32b908\", \"display\":\"Consultation 03/29/2021\" } ] }, \"encounterProviders\":[ { \"uuid\":\"2db62125-4c31-48e8-ac57-55729e3396f4\", " 
                                + "\"provider\":{ \"uuid\":\"c1c26908-3f10-11e4-adec-0800271c1b75\", \"display\":\"superman - Super Man\" }, \"encounterRole\":{ \"uuid\":\"a0b03050-c99b-11e0-9572-0800200c9a66\", \"display\":\"Unknown\" } } ] }";

        
        String orderJSON = "{ \"uuid\": \"c4d93af6-2edc-4e3b-85fb-d94799af7d3a\", \"orderNumber\": \"ORD-307\", \"accessionNumber\": null, \"patient\": { \"uuid\": \"22e546bf-5994-414d-984e-f2a9c03e37fc\", "
                           + "\"display\": \"WWW445427 - asd asd asd\" }, \"concept\": { \"uuid\": \"4e905b9d-83f6-43c6-b388-0e1f9490c39b\", \"display\": \"Leukocytes\" }, \"action\": \"NEW\", \"previousOrder\": null, "
                           + "\"dateActivated\": \"2021-03-31T14:03:15.000+0000\", \"scheduledDate\": null, \"dateStopped\": null, \"autoExpireDate\": \"2021-03-31T15:03:15.000+0000\", \"encounter\": { \"uuid\": \"03c41574-9e2e-494a-8b46-909a9a6c509a\", "
                           + "\"display\": \"Consultation 03/31/2021\" }, \"orderer\": { \"uuid\": \"c1c26908-3f10-11e4-adec-0800271c1b75\", \"display\": \"superman - Super Man\" }, \"orderReason\": null, \"orderReasonNonCoded\": null, "
                           + "\"orderType\": { \"uuid\": \"8189b409-3f10-11e4-adec-0800271c1b75\", \"display\": \"Lab Order\", \"name\": \"Lab Order\", \"javaClassName\": \"org.openmrs.Order\", \"retired\": false, " 
                           + "\"description\": \"An order for laboratory tests\", \"conceptClasses\": [ { \"uuid\": \"33a6291c-8a92-11e4-977f-0800271c1b75\", \"display\": \"LabTest\" }, { \"uuid\": \"8d492026-c2cc-11de-8d13-0010c6dffd0f\", "
                           + "\"display\": \"LabSet\" } ], \"parent\": null }, \"urgency\": \"ROUTINE\", \"instructions\": null, \"commentToFulfiller\": null, \"display\": \"Leukocytes\", \"type\": \"order\", \"resourceVersion\": \"1.10\" }";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode encounterJSONNode = objectMapper.readTree(orderEncounter);
        OpenMRSPatient openMRSPatient = new OpenMRSPatient();
        openMRSPatient.setPatientUUID("UUID-TEST-UUID-TEST");
        OpenMRSOrder openMRSOrder = new OrderMapper().map(orderJSON);;
        OpenMRSConcept concept = new OpenMRSConcept();
        concept.setUuid("uuid");
        OpenMRSEncounter openMRSEncounter = new OpenMRSEncounter();
        openMRSEncounter.setEncounterUuid("UUID-TEST-UUID-TEST");
        openMRSEncounter.setEncounterType(encounterLabResultUUID);
        openMRSEncounter.setEncounterRole(encounterRoleUUID);
        OpenMRSObs obs = new OpenMRSObs();
        OpenMRSObs obsGroupMember = new OpenMRSObs();
        obs.setConcept(concept);
        obsGroupMember.setValue(1.0);
        obsGroupMember.setConcept(concept);
        obs.setGroupMembers(Arrays.asList(obsGroupMember));
        openMRSEncounter.setObs(Arrays.asList(obs));

        ORUHandler.fillOpenMRSEncounter(openMRSEncounter, openMRSOrder, encounterJSONNode, providerUUID, encounterLabResultUUID, encounterRoleUUID, orderEncounter);

        assertEquals("UUID-TEST-UUID-TEST", encounterRoleUUID);
        assertEquals("UUID-TEST-UUID-TEST", openMRSEncounter.getEncounterType());
        assertEquals("c4d93af6-2edc-4e3b-85fb-d94799af7d3a", openMRSEncounter.getOrders().get(0).getUuid());
    }
}
