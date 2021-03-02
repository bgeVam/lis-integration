package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.group.ORM_O01_PATIENT;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.model.v25.segment.*;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSProvider;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.exception.HL7MessageException;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class HL7Service {

    @Autowired
    private OrderRepository orderRepository;


    public HL7Service() {
    }

    public HL7Service(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    private final String NEW_ORDER = "NW";
    private final String CANCEL_ORDER = "CA";
    private final String SENDER = "BahmniEMR";

    public AbstractMessage createMessage(OpenMRSOrder order, Sample sample, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException {
        if(order.isDiscontinued()) {
            return cancelOrderMessage(order, sample, openMRSPatient, providers);
        } else {
            return createOrderMessage(order, sample, openMRSPatient, providers);
        }
    }

    private AbstractMessage createOrderMessage(OpenMRSOrder order, Sample sample, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException {
        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = order.getOrderNumber();
        if(isSizeExceedingLimit(orderNumber)) {
            throw new HL7MessageException("Unable to create HL7 message. Order Number size exceeds limit " + orderNumber);
        }
        orc.getQuantityTiming(0).getPriority().setValue(order.getUrgency());
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderNumber);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderNumber); //accession number - should be of length 16 bytes
        orc.getEnteredBy(0).getGivenName().setValue(SENDER);
        orc.getOrderControl().setValue(NEW_ORDER);

        addOBRComponent(order, sample, message);
        return message;
    }

    private boolean isSizeExceedingLimit(String orderNumber) {
        return orderNumber.getBytes().length > 16;
    }

    private AbstractMessage cancelOrderMessage(OpenMRSOrder order, Sample sample, OpenMRSPatient openMRSPatient, List<OpenMRSProvider> providers) throws DataTypeException {
        Order previousOrder = orderRepository.findByOrderUuid(order.getPreviousOrderUuid());
        if(previousOrder == null) {
            throw  new HL7MessageException("Unable to Cancel the Order. Previous order is not found" + order.getOrderNumber());
        }
        ORM_O01 message = new ORM_O01();
        addMessageHeader(order, message);
        addPatientDetails(message, openMRSPatient);
        addProviderDetails(providers, message);

        // handle ORC component
        ORC orc = message.getORDER().getORC();
        String orderNumber = previousOrder.getOrderNumber();
        if(isSizeExceedingLimit(order.getOrderNumber())) {
            throw new HL7MessageException("Unable to create HL7 message. Order Number size exceeds limit" + orderNumber);
        }
        orc.getPlacerOrderNumber().getEntityIdentifier().setValue(orderNumber);
        orc.getFillerOrderNumber().getEntityIdentifier().setValue(orderNumber); //accession number - should be of length 16 bytes
        orc.getEnteredBy(0).getGivenName().setValue(SENDER);
        orc.getOrderControl().setValue(CANCEL_ORDER);

        addOBRComponent(order, sample, message);
        return message;
    }

    private void addMessageHeader(OpenMRSOrder order, ORM_O01 message) throws DataTypeException {
        MSH msh = message.getMSH();

        msh.getMessageControlID().setValue(generateMessageControlID(order.getOrderNumber()));
        populateMessageHeader(msh, new Date(), "ORM", "O01", SENDER);
    }

    private void addOBRComponent(OpenMRSOrder order, Sample sample, ORM_O01 message) throws DataTypeException {
        // handle OBR component
        OBR obr = message.getORDER().getORDER_DETAIL().getOBR();

        OpenMRSConceptMapping lisConceptSource = order.getLisConceptSource();
        if(lisConceptSource == null) {
            throw new HL7MessageException("Unable to create HL7 message. Missing concept source for order" + order.getUuid());
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

    private void addProviderDetails(List<OpenMRSProvider> providers, ORM_O01 message) throws DataTypeException {
        OpenMRSProvider openMRSProvider = providers.get(0);
        ORC orc = message.getORDER().getORC();
        orc.getOrderingProvider(0).getGivenName().setValue(openMRSProvider.getName());
        orc.getOrderingProvider(0).getIDNumber().setValue(openMRSProvider.getUuid());
    }

    private void addPatientDetails(ORM_O01 message, OpenMRSPatient openMRSPatient) throws DataTypeException {
        // handle the patient PID component
        ORM_O01_PATIENT patient = message.getPATIENT();
        PID pid = patient.getPID();
        pid.getPatientIdentifierList(0).getIDNumber().setValue(openMRSPatient.getPatientId());
        pid.getPatientName(0).getGivenName().setValue(openMRSPatient.getGivenName());
        pid.getPatientName(0).getFamilyName().getSurname().setValue(openMRSPatient.getFamilyName());
        pid.getDateTimeOfBirth().getTime().setValue(openMRSPatient.getBirthDate());
        pid.getAdministrativeSex().setValue(openMRSPatient.getGender());

        message.getORDER().getORDER_DETAIL().getOBR().getPlannedPatientTransportComment(0).getText().setValue(openMRSPatient.getGivenName()+","+openMRSPatient.getFamilyName());

    }

    private static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHH");
    }

    private MSH populateMessageHeader(MSH msh, Date dateTime, String messageType, String triggerEvent, String sendingFacility) throws DataTypeException {
        msh.getFieldSeparator().setValue("|");
        msh.getEncodingCharacters().setValue("^~\\&");
        msh.getSendingFacility().getHd1_NamespaceID().setValue(sendingFacility);
        msh.getSendingFacility().getUniversalID().setValue(sendingFacility);
        msh.getSendingFacility().getNamespaceID().setValue(sendingFacility);
        msh.getDateTimeOfMessage().getTs1_Time().setValue(getHl7DateFormat().format(dateTime));
        msh.getMessageType().getMessageCode().setValue(messageType);
        msh.getMessageType().getTriggerEvent().setValue(triggerEvent);
        //  TODO: do we need to send Message Control ID?
        msh.getProcessingID().getProcessingID().setValue("P");  // stands for production (?)
        msh.getVersionID().getVersionID().setValue("2.5");
        return msh;
    }

    String generateMessageControlID(String orderNumber) {
        int endAt = (orderNumber.length() < 9) ? orderNumber.length() : 9;
        return (new Date().getTime() + orderNumber.substring(4, endAt));
    }

}
