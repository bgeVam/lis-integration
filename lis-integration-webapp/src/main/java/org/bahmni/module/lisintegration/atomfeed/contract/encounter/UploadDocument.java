package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import org.bahmni.module.lisintegration.services.PostResult;

public class UploadDocument implements PostResult {
    private String content;
    private String format;
    private String patientUuid;
    private String encounterTypeName;
    private String fileType;

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPatientUuid() {
        return this.patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public String getEncounterTypeName() {
        return this.encounterTypeName;
    }

    public void setEncounterTypeName(String encounterTypeName) {
        this.encounterTypeName = encounterTypeName;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String getPostUrl(String urlPrefix) {
        return urlPrefix + "/openmrs/ws/rest/v1/bahmnicore/visitDocument/uploadDocument";
    }
}
