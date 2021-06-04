package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.*;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Diagnosis;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.exception.HL7MessageException;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class HL7Service {

    @Autowired
    private OrderRepository orderRepository;

    @Value("${diagnosis_coding_method}")
    private String diagnosisCodingMethod;

    public HL7Service() {
    }

    public HL7Service(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private final String newOrder = "NW";
    private final String cancelOrder = "CA";
    private final String sender = "BahmniEMR";
    private final String email = "root@Example-lis.com";

    /**
     * This method creates the HL7 message.
     *
     * @param order is the object of {@link OpenMRSOrder)
     * @param diagnosisList is the list {@link List} object of {@link Diagnosis)
     * @param sample is the object of {@link Sample)
     * @param openMRSPatient is the object of {@link OpenMRSPatient)
     * @param providers is the list {@link List} object of {@link OpenMRSProvider)
     * @return if order is discontinued method returns cancelOrderMessage if not it returns createOrderMessage
     * @throws HL7Exception if the message cannot be created via
     *                      {@link #createOrderMessage(order, diagnosisList, sample, openMRSPatient, providers)}
     *                      method
     * @throws IOException if the message cannot be created via
     *                     {@link #cancelOrderMessage(order, sample, openMRSPatient, providers)}
     *                     {@link #createOrderMessage(order, diagnosisList, sample, openMRSPatient, providers)}
     *                     method
     */
    public AbstractMessage createMessage(OpenMRSOrder order, List<Diagnosis> diagnosisList, Sample sample,
            OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws HL7Exception, IOException {
        if (order.isDiscontinued()) {
            return cancelOrderMessage(order, sample, openMRSPatient, providers);
        } else {
            return createOrderMessage(order, diagnosisList, sample, openMRSPatient, providers);
        }
    }

    /**
     * This method creates order message to the HL7 message.
     *
     * @param order          is the object of {@link OpenMRSOrder)
     * @param diagnosisList  is the list {@link List} object of {@link Diagnosis)
     * @param sample         is the object of {@link Sample)
     * @param openMRSPatient is the object of {@link OpenMRSPatient)
     * @param providers      represents the list of the providers
     * @return message returns the message which is created by this method
     * @throws DataTypeException if there is a problem with the data type
     *                           {@link #addPatientDetails(message, openMRSPatient)}
     *                           {@link #addProviderDetails(providers, message)}
     *                           method
     * @throws IOException if the message cannot be created via
     *                     {@link #createOrderMessage(order, diagnosisList, sample, openMRSPatient, providers)}
     *                     method
     */
    private AbstractMessage createOrderMessage(OpenMRSOrder order, List<Diagnosis> diagnosisList, Sample sample,
            OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws HL7Exception, IOException {
        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);
        for (int diagnosis = 0; diagnosis < diagnosisList.size(); diagnosis++) {
            addDiagnosis(message, diagnosisList.get(diagnosis), diagnosis);
        }

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = order.getOrderNumber();
        String placerOrderUuid = order.getUuid();
        if (isSizeExceedingLimit(orderNumber)) {
            throw new HL7MessageException(
                    "Unable to create HL7 message. Order Number size exceeds limit " + orderNumber);
        }
        orc.getQuantityTiming(0).getPriority().setValue(order.getUrgency());
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(placerOrderUuid);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue("");
        orc.getEnteredBy(0).getGivenName().setValue(sender);
        orc.getOrderControl().setValue(newOrder);

        addOBRComponent(order, sample, message);
        return message;
    }

    private boolean isSizeExceedingLimit(String orderNumber) {
        return orderNumber.getBytes().length > 16;
    }

    /**
     * processes the message of a canceled order message
     *
     * @param order          is the object of {@link OpenMRSOrder)
     * @param diagnosis      is the list {@link List} object of {@link Diagnosis)
     * @param sample         is the object of {@link Sample)
     * @param openMRSPatient is the object of {@link OpenMRSPatient)
     * @param providers      represents the list of the providers
     * @return message returns the message which is created by this method after
     *         being canceled
     * @throws DataTypeException if there is a problem with the data type
     * @throws IOException if there is a problem with addProviderDetails
     */
    private AbstractMessage cancelOrderMessage(OpenMRSOrder order, Sample sample, OpenMRSPatient openMRSPatient,
            List<OpenMRSProvider> providers) throws DataTypeException, IOException {
        Order previousOrder = orderRepository.findByPlacerOrderUuid(order.getPreviousOrderUuid());
        if (previousOrder == null) {
            throw new HL7MessageException(
                    "Unable to Cancel the Order. Previous order is not found" + order.getOrderNumber());
        }
        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = previousOrder.getOrderNumber();
        String placerOrderUuid = previousOrder.getPlacerOrderUuid();
        String fillerOrderUuid = previousOrder.getFillerOrderUuid();
        if (isSizeExceedingLimit(order.getOrderNumber())) {
            throw new HL7MessageException(
                    "Unable to create HL7 message. Order Number size exceeds limit" + orderNumber);
        }
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(placerOrderUuid);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(fillerOrderUuid);
        orc.getEnteredBy(0).getGivenName().setValue(sender);
        orc.getOrderControl().setValue(cancelOrder);

        addOBRComponent(order, sample, message);
        return message;
    }

    private void addMessageHeader(OpenMRSOrder order, ORM_O01 message) throws DataTypeException {
        MSH msh = message.getMSH();

        msh.getMessageControlID().setValue(generateMessageControlID(order.getOrderNumber()));
        populateMessageHeader(msh, new Date(), "ORM", "O01", sender);
    }

    private void addOBRComponent(OpenMRSOrder order, Sample sample, ORM_O01 message) throws DataTypeException {
        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();

        OpenMRSConceptMapping lisConceptSource = order.getLisConceptSource();
        if (lisConceptSource == null) {
            throw new HL7MessageException(
                    "Unable to create HL7 message. Missing concept source for order" + order.getUuid());
        }
        obr.getUniversalServiceIdentifier().getIdentifier().setValue(lisConceptSource.getCode());
        obr.getUniversalServiceIdentifier().getText().setValue(lisConceptSource.getName());
        obr.getReasonForStudy(0).getText().setValue(order.getCommentToFulfiller());
        obr.getCollectorSComment(0).getText().setValue(order.getConcept().getName().getName());
        obr.getObr7_ObservationDateTime().getTime().setValue(order.getDateCreated());
        obr.getSpecimenSource().getSpecimenSourceNameOrCode().getText().setValue(sample.getName());

        if ("LabSet".equals(order.getConcept().getConceptClass())) {
            obr.getCollectorSComment(0).getText().setValue("Observation Request is Panel Test");
        }
    }

    /**
     * This method adds details of provider to the HL7 message.
     *
     * @param providers is the list {@link List} object of {@link Diagnosis)
     * @param message is the object of {@link OpenMRSOrder)
     * @throws DataTypeException if there is a problem with the data type
     * @throws IOException if the message cannot be created via
     *                     {@link #getPersonUuidByProviderUuid(OpenMRSProvider)}
     *                     {@link #getPerson(OpenMRSService)}
     */
    private void addProviderDetails(List<OpenMRSProvider> providers, ORM_O01 message)
            throws DataTypeException, IOException {
        OpenMRSService openMRSService = new OpenMRSService();
        OpenMRSProvider openMRSProvider = providers.get(0);
        ORC orc = message.getORDER().getORC();

        String personUUID = openMRSService.getPersonUuidByProviderUuid(openMRSProvider.getUuid());
        orc.getOrderingProvider(0).getGivenName().setValue(openMRSService.getPerson(personUUID).getGivenName());
        orc.getOrderingProvider(0).getFamilyName().getSurname()
                .setValue(openMRSService.getPerson(personUUID).getFamilyName());
        orc.getOrderingProvider(0).getIDNumber().setValue(openMRSProvider.getUuid());
        orc.getCallBackPhoneNumber(0).getEmailAddress().setValue(email);
    }

    /**
     * adds the details of the Patient to the HL7 message
     *
     * @param message represents the ORM message
     * @param openMRSPatient represents the patient which is linked to these details
     * @throws DataTypeException used to handle exceptions related with presented data
     */
    private void addPatientDetails(ORM_O01 message, OpenMRSPatient openMRSPatient) throws DataTypeException {
        ORM_O01_PATIENT patient = message.getPATIENT();
        PID pid = patient.getPID();
        pid.getPatientIdentifierList(0).getIDNumber().setValue(openMRSPatient.getPatientId());
        pid.getDriverSLicenseNumberPatient().getLicenseNumber().setValue(openMRSPatient.getDrivingLicenseNumber());
        pid.getSSNNumberPatient().setValue(openMRSPatient.getSSNNumber());
        pid.getPatientName(0).getGivenName().setValue(openMRSPatient.getGivenName());
        pid.getPatientName(0).getFamilyName().getSurname().setValue(openMRSPatient.getFamilyName());
        pid.getDateTimeOfBirth().getTime().setValue(openMRSPatient.getBirthDate());
        pid.getAdministrativeSex().setValue(openMRSPatient.getGender());
        pid.getPatientAddress(0).getCity().setValue(openMRSPatient.getCity());
        pid.getPatientAddress(0).getCountry().setValue(openMRSPatient.getState());
        pid.getPatientAddress(0).getStateOrProvince().setValue(openMRSPatient.getDistrict());

        message.getORDER().getORDER_DETAIL().getOBR().getPlannedPatientTransportComment(0).getText()
                .setValue(openMRSPatient.getGivenName() + "," + openMRSPatient.getFamilyName());
    }

    private static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHH");
    }

    private MSH populateMessageHeader(MSH msh, Date dateTime, String messageType, String triggerEvent,
            String sendingFacility) throws DataTypeException {
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingFacility().getHd1_NamespaceID().setValue(sendingFacility);
        msh.getSendingFacility().getUniversalID().setValue(sendingFacility);
        msh.getSendingFacility().getNamespaceID().setValue(sendingFacility);
        msh.getDateTimeOfMessage().getTs1_Time().setValue(getHl7DateFormat().format(dateTime));
        msh.getMessageType().getMessageCode().setValue(messageType);
        msh.getMessageType().getTriggerEvent().setValue(triggerEvent);
        // do we need to send Message Control ID?
        msh.getProcessingID().getProcessingID().setValue("P"); // stands for production (?)
        msh.getVersionID().getVersionID().setValue("2.5");
        return msh;
    }

    /**
     * adds the details of the Diagnosis to the HL7 message.
     *
     * @param message represents the ORM message.
     * @param diagnosis represents the diagnosis which is linked to these details.
     * @param order order of the diagnosis.
     * @return message returns the segment which is created.
     * @throws HL7Exception represents an exception encountered while processing an HL7 message.
     */
    public DG1 addDiagnosis(ORM_O01 message, Diagnosis diagnosis, Integer order) throws HL7Exception {
        Integer orderDiagnos = order + 1;
        DG1 dg1 = message.getORDER().getORDER_DETAIL().getDG1(order);

        dg1.getSetIDDG1().setValue(orderDiagnos.toString());
        dg1.getDiagnosisCodingMethod().setValue(diagnosisCodingMethod);
        dg1.getDiagnosisCodeDG1().getIdentifier().setValue(diagnosis.getCode());
        dg1.getDiagnosisCodeDG1().getText().setValue(diagnosis.getName());
        dg1.getDiagnosisType().setValue(diagnosis.getType());
        String dateDiagnosis = getHl7DateFormat().format(diagnosis.getDate());
        dg1.getDiagnosisDateTime().getTime().setValue(dateDiagnosis);

        return dg1;
    }

    String generateMessageControlID(String orderNumber) {
        int endAt = (orderNumber.length() < 9) ? orderNumber.length() : 9;
        return (new Date().getTime() + orderNumber.substring(4, endAt));
    }

}
