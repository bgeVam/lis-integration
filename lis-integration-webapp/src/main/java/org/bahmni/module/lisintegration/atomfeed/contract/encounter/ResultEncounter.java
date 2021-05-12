package org.bahmni.module.lisintegration.atomfeed.contract.encounter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.bahmni.module.lisintegration.services.PostResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultEncounter implements PostResult {
    private String patient;
    private String provider;
    private String visit;
    private String encounterType;
    private String encounterRole;
    private List<Observation> obs;
    private List<EncounterProvider> encounterProviders;

    public List<EncounterProvider> getEncounterProviders() {
        return encounterProviders;
    }

    public void setEncounterProviders(List<EncounterProvider> encounterProviders) {
        this.encounterProviders = encounterProviders;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getVisit() {
        return visit;
    }

    public void setVisit(String visit) {
        this.visit = visit;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getEncounterRole() {
        return encounterRole;
    }

    public void setEncounterRole(String encounterRole) {
        this.encounterRole = encounterRole;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Observation> getObs() {
        return obs;
    }

    public void setObs(List<Observation> obs) {
        this.obs = obs;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    @Override
    public String getPostUrl(String urlPrefix) {
        return urlPrefix + "/openmrs/ws/rest/v1/encounter";
    }
}
