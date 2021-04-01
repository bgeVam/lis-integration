package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class PatientDocument {
    private String conctent;
    private String encounterTypeName;
    private String dateTime;

    public String getConctent() {
        return this.conctent;
    }

    public void setConctent(String conctent) {
        this.conctent = conctent;
    }

    public String getEncounterTypeName() {
        return this.encounterTypeName;
    }

    public void setEncounterTypeName(String encounterTypeName) {
        this.encounterTypeName = encounterTypeName;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
