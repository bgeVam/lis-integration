package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

public class EncounterProvider {
    private String provider;
    private String encounterRole;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEncounterRole() {
        return encounterRole;
    }

    public void setEncounterRole(String encounterRole) {
        this.encounterRole = encounterRole;
    }
}
