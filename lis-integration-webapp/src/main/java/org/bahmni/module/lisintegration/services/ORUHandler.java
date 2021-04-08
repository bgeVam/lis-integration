package org.bahmni.module.lisintegration.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Document;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.ResultEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.UploadDocument;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Visit;
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
            OpenMRSService service = new OpenMRSService();
            OpenMRSOrder openMRSOrder = service.getOrder(openMRSEncounter.getOrders().get(0).getUuid());
            String orderEncounter = service.getEncounterByUUID(openMRSOrder.getEncounter().getEncounterUuid());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode encounterJSONNode = objectMapper.readTree(orderEncounter);

            fillOpenMRSEncounter(openMRSEncounter, openMRSOrder, encounterJSONNode, providerUUID,
                    encounterLabResultUUID, encounterRoleUUID, orderEncounter);

            ResultEncounter result = new ResultMapper().map(openMRSEncounter);

            service.postResultEncounter(result);

            // Fetching patient document
            UploadDocument uploadDocument = generateUploadDocument(openMRSEncounter, formatFilePDF);

            String urlUploadDocument = service.urlUploadDocument(uploadDocument);
            JsonNode urlJSON = objectMapper.readTree(urlUploadDocument);
            String image = urlJSON.path("url").getTextValue();

            String patientDocumentTypeUUID = service.getPatientDocumentTypeUuid("Consultations/Summaries");
            VisitDocument visitDocument = generateVisitDocument(openMRSEncounter, encounterJSONNode, image,
                    patientDocumentTypeUUID, encounterPatientDocumentUUID);

            service.postVisitDocument(visitDocument);

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

        Visit visit = new Visit();
        visit.setUuid(encounterJSONNode.path("visit").path("uuid").getTextValue());

        openMRSEncounter.setVisit(visit);
        openMRSEncounter.getObs().get(0).getConcept().setUuid(openMRSOrder.getConcept().getUuid());
        openMRSEncounter.getObs().get(0).getGroupMembers().get(0).getConcept().setUuid(openMRSOrder.getConceptUUID());
    }
}
