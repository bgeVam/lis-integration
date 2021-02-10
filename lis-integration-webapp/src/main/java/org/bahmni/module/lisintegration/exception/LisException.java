package org.bahmni.module.lisintegration.exception;

import org.bahmni.module.lisintegration.model.Lis;

public class LisException extends RuntimeException {

    private String responseMessage;
    private Lis lis;

    public LisException(String responseMessage, Lis lis) {
        super();
        this.responseMessage = responseMessage;
        this.lis = lis;
    }

    public String getMessage() {
        return "Unable to send the message to the lis \n" + lis.toString() + "\n" + responseMessage;
    }
}
