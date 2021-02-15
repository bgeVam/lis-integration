package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.*;
import ca.uhn.hl7v2.parser.PipeParser;
import org.bahmni.module.lisintegration.exception.LisException;
import org.bahmni.module.lisintegration.model.Lis;
import org.bahmni.module.lisintegration.repository.OrderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LisService {

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    public String sendMessage(AbstractMessage message, String orderType) throws HL7Exception, LLPException, IOException {
        Lis lis = orderTypeRepository.getByName(orderType).getLis();
        Message response = post(lis, message);
        String responseMessage = parseResponse(response);
        if (response instanceof ORR_O02) {
            ORR_O02 acknowledgement = (ORR_O02) response;
            String acknowledgmentCode = acknowledgement.getMSA().getAcknowledgmentCode().getValue();
            processAcknowledgement(lis, responseMessage, acknowledgmentCode);
        }
        else if (response instanceof ACK) {
            ACK acknowledgement = (ACK) response;
            String acknowledgmentCode = acknowledgement.getMSA().getAcknowledgmentCode().getValue();
            processAcknowledgement(lis, responseMessage, acknowledgmentCode);
        }
        else {
            throw new LisException(responseMessage, lis);
        }
        return responseMessage;
    }

    Message post(Lis lis, Message requestMessage) throws LLPException, IOException, HL7Exception {
        Connection newClientConnection = null;
        try {
            HapiContext hapiContext = new DefaultHapiContext();
            newClientConnection = hapiContext.newClient(lis.getIp(), lis.getPort(), false);
            Initiator initiator = newClientConnection.getInitiator();
            return initiator.sendAndReceive(requestMessage);
        } finally {
            if (newClientConnection != null) {
                newClientConnection.close();
            }
        }
    }

    String parseResponse(Message response) throws HL7Exception {
        return new PipeParser().encode(response);
    }

    private void processAcknowledgement(Lis lis, String responseMessage, String acknowledgmentCode) {
        if (!AcknowledgmentCode.AA.toString().equals(acknowledgmentCode)) {
            throw new LisException(responseMessage, lis);
        }
    }
}
