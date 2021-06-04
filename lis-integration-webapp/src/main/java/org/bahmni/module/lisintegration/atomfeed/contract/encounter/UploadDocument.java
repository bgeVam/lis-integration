package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import org.bahmni.module.lisintegration.services.PostResult;

public class UploadDocument implements PostResult {
    private String content;
    private String format;
    private String patientUuid;
    private String encounterTypeName;
    private String fileType;

    /**
     * This method gets the content of the document.
     *
     * @return String
     */
    public String getContent() {
        return this.content;
    }

    /**
     * This method sets the content of the document.
     *
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * This method gets the format of the document.
     *
     * @return String
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * This method sets the format of the document.
     *
     * @param format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * This method gets the patient uuid.
     *
     * @return String
     */
    public String getPatientUuid() {
        return this.patientUuid;
    }

    /**
     * This method sets the patient uuid.
     *
     * @param patientUuid
     */
    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    /**
     * This method gets the encounter type name.
     *
     * @return String
     */
    public String getEncounterTypeName() {
        return this.encounterTypeName;
    }

    /**
     * This method sets the encounter type name.
     *
     * @param encounterTypeName
     */
    public void setEncounterTypeName(String encounterTypeName) {
        this.encounterTypeName = encounterTypeName;
    }

    /**
     * This method gets the patient file type.
     *
     * @return String
     */
    public String getFileType() {
        return this.fileType;
    }

    /**
     * This method sets the file type.
     *
     * @param fileType
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * This method getPostUrl get the url prefix.
     *
     * @return String
     */
    @Override
    public String getPostUrl(String urlPrefix) {
        return urlPrefix + "/openmrs/ws/rest/v1/bahmnicore/visitDocument/uploadDocument";
    }
}
