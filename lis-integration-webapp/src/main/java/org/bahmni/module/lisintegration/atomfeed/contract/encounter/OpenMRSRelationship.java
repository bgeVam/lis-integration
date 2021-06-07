package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class OpenMRSRelationship {
    private OpenMRSPerson doctor;

    public OpenMRSPerson getDoctor() {
        return this.doctor;
    }

    public void setDoctor(OpenMRSPerson doctor) {
        this.doctor = doctor;
    }
}
