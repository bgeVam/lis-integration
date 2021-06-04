package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class OpenMRSPerson {
    private String personUUID;
    private String givenName;
    private String familyName;

    /**
     * This method gets the uuid of the person.
     *
     * @return String
     */
    public String getPersonUUID() {
        return this.personUUID;
    }

    /**
     * This method sets the uuid of the person.
     *
     * @param personUUID
     */
    public void setPersonUUID(String personUUID) {
        this.personUUID = personUUID;
    }

    /**
     * This method gets the given name of the person.
     *
     * @return String
     */
    public String getGivenName() {
        return this.givenName;
    }

    /**
     * This method sets the given name of the person.
     *
     * @param givenName
    */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * This method gets the family name of the person.
     *
     * @return String
     */
    public String getFamilyName() {
        return this.familyName;
    }

    /**
     * This method sets the family name of the person.
     *
     * @param familyName
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
