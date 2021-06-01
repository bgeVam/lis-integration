package org.bahmni.module.lisintegration.services;
import ca.uhn.hl7v2.validation.builder.support.DefaultValidationBuilder;

@SuppressWarnings("serial")
public class DefaultValidationWithExtendedISDataTypeLength extends DefaultValidationBuilder {

    /**
     * This function configures the length of the hl7 segment from the
     * defaul 200 characters to 99999 characters
     */
    @Override
    protected void configure() {

        forAllVersions()
            .primitive("IS").is(maxLength(99999));
    }
}
