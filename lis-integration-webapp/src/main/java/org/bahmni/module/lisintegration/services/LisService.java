package org.bahmni.module.lisintegration.services;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.bahmni.module.lisintegration.exception.LisException;
import org.bahmni.module.lisintegration.model.Lis;
import org.bahmni.module.lisintegration.repository.OrderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.ConnectionListener;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.model.v25.message.ORR_O02;
import ca.uhn.hl7v2.parser.PipeParser;

@Component
public class LisService {
    private static final org.apache.log4j.Logger log = Logger.getLogger(LisService.class);

    @Value("${green.letters}")
    private String printGreen;

    @Value("${red.letters}")
    private String printRed;

    @Value("${default.letters}")
    private String printDefault;

    @Value("${lis.host}")
    private String host;

    @Value("${lis.port}")
    private Integer port;

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private ORUHandler oruHandler;

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

    public void startServer() throws InterruptedException {
        HapiContext hapiContext = new DefaultHapiContext();
        HL7Service server = hapiContext.newServer(port, false);
        server.registerApplication("ORU", "R01", oruHandler);
        server.setExceptionHandler(new ErrorHandler());
        server.registerConnectionListener(
            new ConnectionListener() {
                @Override
                public void connectionReceived(Connection connection) {
                    log.info(printGreen + "New connection received: " + connection.getRemoteAddress().toString() + printDefault);
                }

                @Override
                public void connectionDiscarded(Connection connection) {
                    log.info(printRed + "Lost connection from: " + connection.getRemoteAddress().toString() + printDefault);
                }
            });
        server.startAndWait();
        System.setProperty("ca.uhn.hl7v2.app.initiator.timeout", Integer.toString(300000));

        log.info(printGreen + "Started server at " + host + ":" + port + " with timeout of " + 300000 + printDefault);
    }

}
