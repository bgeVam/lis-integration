package org.bahmni.module.lisintegration.atomfeed.contract.encounter;
import java.util.List;

import org.bahmni.module.lisintegration.services.PostResult;


public class NoteEncounter implements PostResult {
    private String patient;
    private String provider;
    private String visit;
    private String encounterType;
    private String encounterRole;
    private List<Observation> obs;
    private List<EncounterProvider> encounterProviders;
    private String location;
    private String encounterDatetime;

    /**
     *
     * @return
     */
    public String getLocation() {
        return this.location;
    }

    /**
     *
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return String
     */
    public String getEncounterDatetime() {
        return this.encounterDatetime;
    }

    /**
     *
     * @param encounterDatetime
     */
    public void setEncounterDatetime(String encounterDatetime) {
        this.encounterDatetime = encounterDatetime;
    }

    /**
     *
     * @return String
     */
    public String getPatient() {
        return this.patient;
    }

    /**
     *
     * @param patient
     */
    public void setPatient(String patient) {
        this.patient = patient;
    }

    /**
     *
     * @return String
     */
    public String getProvider() {
        return this.provider;
    }

    /**
     *
     * @param provider
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     *
     * @return String
     */
    public String getVisit() {
        return this.visit;
    }

    /**
     *
     * @param visit
     */
    public void setVisit(String visit) {
        this.visit = visit;
    }

    /**
     *
     * @return String
     */
    public String getEncounterType() {
        return this.encounterType;
    }

    /**
     *
     * @param encounterType
     */
    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    /**
     *
     * @return String
     */
    public String getEncounterRole() {
        return this.encounterRole;
    }

    /**
     *
     * @param encounterRole
     */
    public void setEncounterRole(String encounterRole) {
        this.encounterRole = encounterRole;
    }

    /**
     *
     * @return List<Observation>
     */
    public List<Observation> getObs() {
        return this.obs;
    }

    /**
     *
     * @param obs
     */
    public void setObs(List<Observation> obs) {
        this.obs = obs;
    }

    /**
     *
     * @return List<EncounterProvider>
     */
    public List<EncounterProvider> getEncounterProviders() {
        return this.encounterProviders;
    }

    /**
     *
     * @param encounterProviders
     */
    public void setEncounterProviders(List<EncounterProvider> encounterProviders) {
        this.encounterProviders = encounterProviders;
    }

    /**
     *
     * @param urlPrefix
     * @return String
     */
    @Override
    public String getPostUrl(String urlPrefix) {
        return urlPrefix + "/openmrs/ws/rest/v1/encounter";
    }
}
