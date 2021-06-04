package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class PatientDocument {
    private String content;
    private String encounterTypeName;
    private String dateTime;

    /**
     * This method gets the content of document.
     *
     * @return String
     */
    public String getContent() {
        return this.content;
    }

    /**
     * This method sets the content of document.
     *
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

     /**
     * This method gets the encounter type.
     *
     * @return String
     */
    public String getEncounterTypeName() {
        return this.encounterTypeName;
    }

    /**
     * This method sets the encounter type.
     *
     * @param encounterTypeName
     */
    public void setEncounterTypeName(String encounterTypeName) {
        this.encounterTypeName = encounterTypeName;
    }

     /**
     * This method gets the created document date and time.
     *
     * @return String
     */
    public String getDateTime() {
        return this.dateTime;
    }

     /**
     * This method gets the created document date and time.
     *
     * @param dateTime
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
