package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSObs {
    public static final String OPENMRS_ENCOUNTER_PROVIDER_PROVIDER_UUID = "7d162c29-3f12-11e4-adec-0800271c1b75";
    public static final String OPENMRS_ENCOUNTER_PROVIDER_PROVIDER_ROLE_UUID = "a0b03050-c99b-11e0-9572-0800200c9a66";

    private String obsDateTime;
    private String name;
    private OpenMRSOrder order;
    private OpenMRSConcept concept;
    private List<OpenMRSObs> groupMembers = new ArrayList<OpenMRSObs>();
    private Double value;

    public OpenMRSObs() {
    }

    public OpenMRSObs(String obsDateTime, OpenMRSConcept concept, OpenMRSOrder order) {
        this.obsDateTime = obsDateTime;
        this.concept = concept;
        this.order = order;
    }

    public OpenMRSObs(String obsDateTime, OpenMRSConcept concept, Double value) {
        this.obsDateTime = obsDateTime;
        this.concept = concept;
        this.value = value;
    }

    public void setGroupMembers(List<OpenMRSObs> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getObsDateTime() {
        return obsDateTime;
    }

    public void setObsDateTime(String obsDateTime) {
        this.obsDateTime = obsDateTime;
    }

    public OpenMRSConcept getConcept() {
        return concept;
    }

    public void setConcept(OpenMRSConcept concept) {
        this.concept = concept;
    }

    public List<OpenMRSObs> getGroupMembers() {
        return groupMembers.isEmpty() ? null : groupMembers;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public OpenMRSOrder getOrder() {
        return order;
    }

    public void setOrder(OpenMRSOrder order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
