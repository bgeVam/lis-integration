package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class OpenMRSPerson {
    private String personUuid;
    private String givenName;
    private String middleName;
    private String familyName;

    /**
     *
     * @return String
     */
    public String getPresonUuid() {
        return this.personUuid;
    }

    /**
     *
     * @param uuid
     */
    public void setPersonUuid(String personUuid) {
        this.personUuid = personUuid;
    }

    /**
     *
     * @return String
     */
    public String getGivenName() {
        return this.givenName;
    }

    /**
     *
     * @param givenName
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     *
     * @return String
     */
    public String getMiddleName() {
        return this.middleName;
    }

    /**
     *
     * @param middleName
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     *
     * @return String
     */
    public String getFamilyName() {
        return this.familyName;
    }

    /**
     * @param familyName
     *
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
