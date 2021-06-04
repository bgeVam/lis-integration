package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class Document {
    private String image;
    private String obsDateTime;
    private String testUuid;

    /**
     * This method gets the image.
     *
     * @return String
     */
    public String getImage() {
        return this.image;
    }

    /**
     * This method sets the image.
     *
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * This method gets the observation date and time.
     *
     * @return String
     */
    public String getObsDateTime() {
        return this.obsDateTime;
    }

    /**
     * This method sets the observation date and time.
     *
     * @param obsDateTime
     */
    public void setObsDateTime(String obsDateTime) {
        this.obsDateTime = obsDateTime;
    }

    /**
     * This method gets the test uuid.
     *
     * @return String
     */
    public String getTestUuid() {
        return this.testUuid;
    }

    /**
     * This method sets the test uuid.
     *
     * @param testUuid
     */
    public void setTestUuid(String testUuid) {
        this.testUuid = testUuid;
    }
}
