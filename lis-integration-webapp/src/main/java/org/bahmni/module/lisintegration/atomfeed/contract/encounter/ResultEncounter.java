package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultEncounter {
    private String patient;
    private String provider;
    private String visit;
    private String encounterType;
    private String encounterRole;
    private List<Observation> Obs;
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
        return Obs;
    }

    public void setObs(List<Observation> obs) {
        Obs = obs;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
