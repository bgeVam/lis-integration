package org.bahmni.module.lisintegration.services;

import org.springframework.stereotype.Component;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;

@Component
public class SharedHapiContext {

    private final HapiContext hapiContext;

    public SharedHapiContext() {
        hapiContext = new DefaultHapiContext();
        // TODO configure the length of the hl7 message
        hapiContext.setValidationContext((ValidationContext) ValidationContextFactory.noValidation());
    }

    public final HapiContext getHapiContext() {
        return this.hapiContext;
    }
}
