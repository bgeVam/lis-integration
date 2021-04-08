package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.protocol.ReceivingApplicationExceptionHandler;
import org.apache.log4j.Logger;

import java.util.Map;

public class ErrorHandler implements ReceivingApplicationExceptionHandler {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(ErrorHandler.class);
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";

    @Override
    public final String processException(String incomingMessage, Map<String, Object> incomingMetadata,
            String outgoingMessage, Exception e) throws HL7Exception {
        LOG.info(ANSI_RED + "ErrorHandler" + ANSI_RESET);
        LOG.error(incomingMessage, e);
        return "error";
    }
}
