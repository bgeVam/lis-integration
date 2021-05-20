package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import org.bahmni.module.lisintegration.atomfeed.client.Constants;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSOrder {
    public static final String ACTION_NEW = "NEW";
    public static final String ACTION_DISCONTINUE = "DISCONTINUE";

    private String action;
    private String uuid;
    private String orderType;
    private Boolean voided;
    private OpenMRSConcept concept;
    private String orderNumber;
    private String previousOrderUuid;
    private String commentToFulfiller;
    private String fillerOrderUuid;
    private String urgency;
    private Date dateCreated;
    private OpenMRSPatient patient;
    private OpenMRSEncounter encounter;

    public String getUrgency() {
        String statusPriority = null;
        if ("STAT".equals(urgency)) {
            statusPriority = "S";
        } else if ("AS SOON AS POSSIBLE".equals(urgency)) {
            statusPriority = "A";
        } else if ("ROUTINE".equals(urgency)) {
            statusPriority = "R";
        } else if ("PREOPERATIVE".equals(urgency)) {
            statusPriority = "P";
        } else if ("TIMING CRITICAL".equals(urgency)) {
            statusPriority = "T";
        } else {
            statusPriority = urgency;
        }
        return statusPriority;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public OpenMRSOrder() {
        this.action = ACTION_NEW;
    }

    public OpenMRSOrder(final String uuid, final String orderType, final OpenMRSConcept concept, final Boolean voided,
            final String action,
            final String previousOrderUuid,
            final String fillerOrderUuid) {
        this.uuid = uuid;
        this.orderType = orderType;
        this.voided = voided;
        this.concept = concept;
        this.action = action != null ? action : ACTION_NEW;
        this.previousOrderUuid = previousOrderUuid;
        this.fillerOrderUuid = fillerOrderUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrderType() {
        return orderType;
    }

    public OpenMRSConcept getConcept() {
        return concept;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setConcept(OpenMRSConcept concept) {
        this.concept = concept;
    }

    public Boolean isVoided() {
        if (voided == null) {
            return false;
        }
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public String getTestName() {
        return concept.getName().getName();
    }

    public String getConceptUUID() {
        return concept.getUuid();
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OpenMRSConceptMapping getLisConceptSource() {
        for (OpenMRSConceptMapping mapping : concept.getMappings()) {
            if (mapping.getSource().equals(Constants.LIS_CONCEPT_SOURCE_NAME)) {
                return mapping;
            }
        }
        return null;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPreviousOrderUuid() {
        return previousOrderUuid;
    }

    public void setPreviousOrderUuid(String previousOrderUuid) {
        this.previousOrderUuid = previousOrderUuid;
    }

    public boolean isDiscontinued() {
        return ACTION_DISCONTINUE.equals(this.action);
    }

    public boolean isNew() {
        return ACTION_NEW.equals(this.action);
    }

    public String getCommentToFulfiller() {
        return commentToFulfiller;
    }

    public void setCommentToFulfiller(String commentToFulfiller) {
        this.commentToFulfiller = commentToFulfiller;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public OpenMRSPatient getPatient() {
        return patient;
    }

    public void setPatient(OpenMRSPatient patient) {
        this.patient = patient;
    }

    public OpenMRSEncounter getEncounter() {
        return encounter;
    }

    public void setEncounter(OpenMRSEncounter encounter) {
        this.encounter = encounter;
    }

    public String getFillerOrderUuid() {
        return fillerOrderUuid;
    }

    public void setFillerOrderUuid(String fillerOrderUuid) {
        this.fillerOrderUuid = fillerOrderUuid;
    }
}
