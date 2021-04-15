package org.bahmni.module.lisintegration.atomfeed.mappers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSObs;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.PatientDocument;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.ORC;

public class HL7ORUtoOpenMRSEncounterMapper {

    public OpenMRSEncounter map(ORU_R01 oru) throws IOException, ParseException, HL7Exception {

        OpenMRSEncounter result = new OpenMRSEncounter();

        for (ORU_R01_PATIENT_RESULT response : oru.getPATIENT_RESULTAll()) {
            for (ORU_R01_ORDER_OBSERVATION orderObservation : response.getORDER_OBSERVATIONAll()) {
                // OBR segment can be used for panel
                OBR obr = orderObservation.getOBR();

                if ("Patient Document".equals(obr.getUniversalServiceIdentifier().getText().getValue())) {
                    for (ORU_R01_OBSERVATION observation : orderObservation.getOBSERVATIONAll()) {
                        OBX obxDocument = observation.getOBX();

                        PatientDocument patientDocument = new PatientDocument();
                        patientDocument.setConctent(obxDocument.getObservationValue()[0].encode());
                        patientDocument.setEncounterTypeName(obr.getUniversalServiceIdentifier().getText().getValue());
                        patientDocument.setDateTime(obxDocument.getObservationValue()[0].encode());

                        result.setPatientDocument(patientDocument);
                    }
                } else {
                    ORC orc = orderObservation.getORC();
                    String fillerOrderNumber = orc.getFillerOrderNumber().getEntityIdentifier().getValue();
                    OpenMRSOrder order = new OpenMRSOrder();
                    order.setUuid(fillerOrderNumber);
                    result.setOrders(Arrays.asList(order));

                    OpenMRSObs obs = new OpenMRSObs();
                    List<OpenMRSObs> openMRSOBSArray = new ArrayList<OpenMRSObs>(
                            orderObservation.getOBSERVATIONAll().size());
                    String alternateText = obr.getUniversalServiceIdentifier().getAlternateText().getValue();

                    for (ORU_R01_OBSERVATION observation : orderObservation.getOBSERVATIONAll()) {
                        OBX obx = observation.getOBX();

                        String observationIdentifier = obx.getObservationIdentifier().getText().getValue();
                        OpenMRSConcept concept = new OpenMRSConcept();
                        concept.setName(new OpenMRSConceptName(observationIdentifier));
                        obs.setConcept(concept);
                        obs.setOrder(order);
                        String observationDateTime = obx.getDateTimeOfTheObservation().getTime().getValue();
                        obs.setObsDateTime(convertHL7DateStringToOpenMRSDateString(observationDateTime));

                        // groupMembers
                        OpenMRSObs valueGroupMember = new OpenMRSObs();
                        if ("Panel".equals(alternateText)) {
                            result.setPanel(true);
                            valueGroupMember.setConcept(concept);
                            valueGroupMember.setValue(Double.valueOf(obx.getObservationValue()[0].encode()));
                            openMRSOBSArray.add(valueGroupMember);
                            obs.setGroupMembers(Arrays.asList(valueGroupMember));
                        } else {
                            valueGroupMember.setConcept(concept);
                            valueGroupMember.setValue(Double.valueOf(obx.getObservationValue()[0].encode()));
                            obs.setGroupMembers(Arrays.asList(valueGroupMember));
                            result.setObs(Arrays.asList(obs));
                        }
                    }
                    if ("Panel".equals(alternateText)) {
                        obs.setGroupMembers(openMRSOBSArray);
                        result.setObs(Arrays.asList(obs));
                    }
                }
            }
        }
        return result;
    }

    private String convertHL7DateStringToOpenMRSDateString(String hl7DateString) throws ParseException {
        DateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = inputDateFormat.parse(hl7DateString);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return outputDateFormat.format(date);
    }
}
