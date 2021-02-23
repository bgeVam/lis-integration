package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSConceptClass {
    private String conceptClass;

    public OpenMRSConceptClass() {
    }

    public OpenMRSConceptClass(String conceptClass) {
        this.conceptClass = conceptClass;
    }

    public String getConceptClass() {
        return conceptClass;
    }

    public void setConceptClass(String conceptClass) {
        this.conceptClass = conceptClass;
    }
}
