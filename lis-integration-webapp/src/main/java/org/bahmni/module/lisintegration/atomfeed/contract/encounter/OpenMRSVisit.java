package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import java.util.ArrayList;
import java.util.Date;

public class OpenMRSVisit {
    private String uuid;
    private Date admissionDate;
    private String visitNumber;
    private OpenMRSOrder order;
    private String latestOrderUuid;

    private ArrayList<OpenMRSRelationship> relationships;

    public OpenMRSVisit() {
    }

    /**
     * This method Gets the relationships of the patient
     * @return ArrayList<OpenMRSRelationship>
     */
    public ArrayList<OpenMRSRelationship> getRelationships() {
        return this.relationships;
    }

    /**
     * This method sets the relationships of the patient
     * @param relationships
     */
    public void setRelationships(ArrayList<OpenMRSRelationship> relationships) {
        this.relationships = relationships;
    }

    /**
     * This method gets the addmiting date of the Patient
     * @return Date
     */
    public Date getAdmissionDate() {
        return this.admissionDate;
    }

    /**
     *  This method sets the addmiting date of the Patient
     * @param admitDate
     */
    public void setAdmissionDate(Date admissionDate) {
        this.admissionDate = admissionDate;
    }

    /**
     * This method gets the Numer of the visit
     * @return String
     */
    public String getVisitNumber() {
        return this.visitNumber;
    }

    /**
     * This method sets the number of the visit
     * @param visitNumber
     */
    public void setVisitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
    }

    /**
     * This method gets the uuid of the visit
     * @return String
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * this method sets the uuid of the visit
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * This method gets the uuid of the latest Order for the patient
     * @return String
     */
    public String getLatestOrderUuid() {
        return this.latestOrderUuid;
    }

    /**
     * This method sets the uuid of the latest order of the patient
     * @param latestOrderUuid
     */
    public void setLatestOrderUuid(String latestOrderUuid) {
        this.latestOrderUuid = latestOrderUuid;
    }

    /**
     * @return OpenMRSOrder
     */
    public OpenMRSOrder getOrder() {
        return this.order;
    }

    /**
     * @param order
     */
    public void setOrder(OpenMRSOrder order) {
        this.order = order;
    }
}
