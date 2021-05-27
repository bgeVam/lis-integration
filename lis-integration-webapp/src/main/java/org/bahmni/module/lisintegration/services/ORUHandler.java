package org.bahmni.module.lisintegration.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Document;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSVisit;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.ResultEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.UploadDocument;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.VisitDocument;
import org.bahmni.module.lisintegration.atomfeed.mappers.HL7ORUtoOpenMRSEncounterMapper;
import org.bahmni.module.lisintegration.atomfeed.mappers.ResultMapper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;

@Component
public class ORUHandler implements ReceivingApplication {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(ORUHandler.class);

    @Value("${provider.lab_system.uuid}")
    private String providerUUID;

    @Value("${encounter.role.unknown.uuid}")
    private String encounterRoleUUID;

    @Value("${encounter.type.lab_result.uuid}")
    private String encounterLabResultUUID;

    @Value("${encounter.type.patient_document.uuid}")
    private String encounterPatientDocumentUUID;

    @Value("pdf")
    private String formatFilePDF;

    @Autowired
    private SharedHapiContext sharedHapiContext;

    @Autowired
    private OpenMRSService openMRSService;

    @Override
    public Message processMessage(Message message, Map<String, Object> stringObjectMap)
            throws ReceivingApplicationException, HL7Exception {
        try {
            LOG.info(message.encode());
            LOG.info("--------------------");

            ORU_R01 oruR01 = (ORU_R01) message;
            HapiContext hapiContext = sharedHapiContext.getHapiContext();

            String encodedMessage = hapiContext.getPipeParser().encode(message);
            LOG.info("Received message:\n" + encodedMessage + "\n\n");

            OpenMRSEncounter openMRSEncounter = new HL7ORUtoOpenMRSEncounterMapper().map(oruR01);

            // Fetching test resault
            OpenMRSOrder openMRSOrder = openMRSService.getOrder(openMRSEncounter.getOrders().get(0).getUuid());
            String orderEncounter = openMRSService.getEncounterByUUID(openMRSOrder.getEncounter().getEncounterUuid());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode encounterJSONNode = objectMapper.readTree(orderEncounter);

            fillOpenMRSEncounter(openMRSEncounter, openMRSOrder, encounterJSONNode, providerUUID,
                    encounterLabResultUUID, encounterRoleUUID, orderEncounter);

            fillObservation(openMRSEncounter, openMRSOrder);
            ResultEncounter result = new ResultMapper().map(openMRSEncounter);

            openMRSService.postResult(result);

            // Fetching patient document
            UploadDocument uploadDocument = generateUploadDocument(openMRSEncounter, formatFilePDF);

            String urlUploadDocument = openMRSService.postResult(uploadDocument);
            JsonNode urlJSON = objectMapper.readTree(urlUploadDocument);
            String image = urlJSON.path("url").getTextValue();

            String patientDocumentTypeUUID = openMRSService.getPatientDocumentTypeUuid("Consultations/Summaries");
            VisitDocument visitDocument = generateVisitDocument(openMRSEncounter, encounterJSONNode, image,
                    patientDocumentTypeUUID, encounterPatientDocumentUUID);

            openMRSService.postResult(visitDocument);

            return message.generateACK();
        } catch (Throwable t) {
            LOG.error("Throwable caught: ", t);
            throw new ReceivingApplicationException(t);
        }
    }

    @Override
    public boolean canProcess(Message message) {
        LOG.info("ORUHandler.canProcess");
        LOG.info(message);
        return true;
    }

    static VisitDocument generateVisitDocument(OpenMRSEncounter openMRSEncounter, JsonNode encounterJSONNode,
            String image, String patientDocumentTypeUUID, String encounterPatientDocumentUUID) {
        Document document = new Document();
        document.setTestUuid(patientDocumentTypeUUID);
        document.setImage(image);

        VisitDocument visitDocument = new VisitDocument();
        visitDocument.setPatientUuid(openMRSEncounter.getOrders().get(0).getPatient().getPatientUUID());
        visitDocument.setVisitTypeUuid(encounterJSONNode.path("visit").path("visitType").path("uuid").getTextValue());
        visitDocument.setVisitStartDate(encounterJSONNode.path("visit").path("startDatetime").getTextValue());
        visitDocument.setEncounterTypeUuid(encounterPatientDocumentUUID);
        visitDocument.setProviderUuid(
                encounterJSONNode.path("encounterProviders").get(0).path("provider").path("uuid").getTextValue());
        visitDocument.setVisitUuid(encounterJSONNode.path("visit").path("uuid").getTextValue());
        visitDocument.setLocationUuid(encounterJSONNode.path("location").path("uuid").getTextValue());
        visitDocument.setDocuments(Arrays.asList(document));
        return visitDocument;
    }

    static UploadDocument generateUploadDocument(OpenMRSEncounter openMRSEncounter, String formatFilePDF) {
        UploadDocument uploadDocument = new UploadDocument();
        uploadDocument.setPatientUuid(openMRSEncounter.getOrders().get(0).getPatient().getPatientUUID());
        uploadDocument.setContent(openMRSEncounter.getPatientDocument().getConctent());
        uploadDocument.setEncounterTypeName(openMRSEncounter.getPatientDocument().getEncounterTypeName());
        uploadDocument.setFileType(formatFilePDF);
        uploadDocument.setFormat(formatFilePDF);
        return uploadDocument;
    }

    static void fillOpenMRSEncounter(OpenMRSEncounter openMRSEncounter, OpenMRSOrder openMRSOrder,
            JsonNode encounterJSONNode, String providerUUID, String encounterLabResultUUID, String encounterRoleUUID,
            String orderEncounter) throws JsonProcessingException, IOException {
        OpenMRSProvider provider = new OpenMRSProvider();
        provider.setUuid(providerUUID);

        openMRSEncounter.setOrders(Arrays.asList(openMRSOrder));
        openMRSEncounter.setEncounterType(encounterLabResultUUID);
        openMRSEncounter.setEncounterRole(encounterRoleUUID);
        openMRSEncounter.setEncounterUuid(openMRSOrder.getEncounter().getEncounterUuid());

        openMRSEncounter.setPatientUuid(openMRSEncounter.getOrders().get(0).getPatient().getPatientUUID());
        openMRSEncounter.setProviders(Arrays.asList(provider));

        OpenMRSVisit visit = new OpenMRSVisit();
        visit.setUuid(encounterJSONNode.path("visit").path("uuid").getTextValue());
        openMRSEncounter.setVisit(visit);
    }
    static void fillObservation(OpenMRSEncounter openMRSEncounter, OpenMRSOrder openMRSOrder) throws IOException {
        ORUHandler oruHandler = new ORUHandler();
        OpenMRSService openMRSService = new OpenMRSService();
        // fill observation for Panel
        if (openMRSEncounter.isPanel()) {
            List<OpenMRSConcept> listOfTests = openMRSService.getTestsOfPanel(openMRSOrder.getConcept().getUuid());

            for (int concept = 0; concept < openMRSEncounter.getObs().get(0).getGroupMembers().size(); concept++) {
                String name = openMRSEncounter.getObs().get(0).getGroupMembers().get(concept).getConcept().getName()
                        .getName();
                openMRSEncounter.getObs().get(0).getGroupMembers().get(concept).getConcept()
                        .setUuid(oruHandler.getUuidOfConcept(name, listOfTests));
            }
            OpenMRSConcept openMRSConcept = new OpenMRSConcept();
            openMRSConcept.setUuid(openMRSOrder.getConcept().getUuid());
            openMRSEncounter.getObs().get(0).setConcept(openMRSConcept);
        // fill observation for Test
        } else {
            openMRSEncounter.getObs().get(0).getConcept().setUuid(openMRSOrder.getConcept().getUuid());
            openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getConcept()
                    .setUuid(openMRSOrder.getConceptUUID());
        }
    }

    public String getUuidOfConcept(String conceptName, List<OpenMRSConcept> conceptList) {
        String conceptUuid = "";
        for (OpenMRSConcept testConcept : conceptList) {
            if (conceptName.equals(testConcept.getName().getName())) {
                conceptUuid = testConcept.getUuid();
                break;
            }
        }
        return conceptUuid;
    }
}
