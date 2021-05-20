package org.bahmni.module.lisintegration.atomfeed.contract.encounter;
import java.util.List;

import org.bahmni.module.lisintegration.services.PostResult;

public class VisitDocument implements PostResult {
    private String patientUuid;
    private String visitTypeUuid;
    private String visitStartDate;
    private String visitUuid;
    private String providerUuid;
    private String encounterDateTime;
    private String encounterTypeUuid;
    private String locationUuid;
    private List<Document> documents;

    public String getPatientUuid() {
        return this.patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public List<Document> getDocuments() {
        return this.documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getVisitTypeUuid() {
        return this.visitTypeUuid;
    }

    public void setVisitTypeUuid(String visitTypeUuid) {
        this.visitTypeUuid = visitTypeUuid;
    }

    public String getVisitStartDate() {
        return this.visitStartDate;
    }

    public void setVisitStartDate(String visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public String getVisitUuid() {
        return this.visitUuid;
    }

    public void setVisitUuid(String visitUuid) {
        this.visitUuid = visitUuid;
    }

    public String getProviderUuid() {
        return this.providerUuid;
    }

    public void setProviderUuid(String providerUuid) {
        this.providerUuid = providerUuid;
    }

    public String getEncounterDateTime() {
        return this.encounterDateTime;
    }

    public void setEncounterDateTime(String encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }

    public String getEncounterTypeUuid() {
        return this.encounterTypeUuid;
    }

    public void setEncounterTypeUuid(String encounterTypeUuid) {
        this.encounterTypeUuid = encounterTypeUuid;
    }

    public String getLocationUuid() {
        return this.locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    @Override
    public String getPostUrl(String urlPrefix) {
        return urlPrefix + "/openmrs/ws/rest/v1/bahmnicore/visitDocument";
    }
}
