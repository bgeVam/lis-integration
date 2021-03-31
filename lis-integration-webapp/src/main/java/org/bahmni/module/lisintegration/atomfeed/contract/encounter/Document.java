package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class Document {
    private String image;
    private String obsDateTime;
    private String testUuid;

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getObsDateTime() {
        return this.obsDateTime;
    }

    public void setObsDateTime(String obsDateTime) {
        this.obsDateTime = obsDateTime;
    }

    public String getTestUuid() {
        return this.testUuid;
    }

    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }
}
