package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSProvider {

    private String uuid;
    private String name;

    public OpenMRSProvider() {
    }

    public OpenMRSProvider(final String uuid, final String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
