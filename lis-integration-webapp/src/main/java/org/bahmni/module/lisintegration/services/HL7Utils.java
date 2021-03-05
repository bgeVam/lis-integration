package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.model.v25.segment.MSH;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HL7Utils {

    public static DateFormat getHl7DateFormat() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static MSH populateMessageHeader(MSH msh, Date dateTime, String messageType, String triggerEvent, String sendingFacility) throws DataTypeException {
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

    public static ACK generateACK(String messageControlId, String sendingFacility) throws DataTypeException {
        ACK ack = new ACK();

        populateMessageHeader(ack.getMSH(), new Date(), "ACK", null, sendingFacility);

        ack.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AA.getMessage());
        ack.getMSA().getMessageControlID().setValue(messageControlId);

        return ack;
    }

    public static ACK generateErrorACK(String messageControlId, String sendingFacility, String errorMessage) throws DataTypeException {
        ACK ack = new ACK();

        populateMessageHeader(ack.getMSH(), new Date(), "ACK", null, sendingFacility);

        ack.getMSA().getAcknowledgmentCode().setValue("AR");
        ack.getMSA().getMessageControlID().setValue(messageControlId);
        ack.getMSA().getTextMessage().setValue(errorMessage);

        return ack;
    }
}
