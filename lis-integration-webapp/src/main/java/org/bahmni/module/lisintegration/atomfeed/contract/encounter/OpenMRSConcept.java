package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSConcept {
    private String uuid;
    private OpenMRSConceptName name;
    private boolean set;
    private List<OpenMRSConceptMapping> mappings;
    private String conceptClass;

    public OpenMRSConcept() {
    }

    public OpenMRSConcept(final String uuid, final OpenMRSConceptName name, final boolean set) {

        this.uuid = uuid;
        this.name = name;
        this.set = set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public List<OpenMRSConceptMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<OpenMRSConceptMapping> mappings) {
        this.mappings = mappings;
    }


    public String getUuid() {
        return uuid;
    }

    public OpenMRSConceptName getName() {
        return name;
    }

    public void setName(OpenMRSConceptName name) {
        this.name = name;
    }

    public boolean isSet() {
        return set;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OpenMRSConcept that = (OpenMRSConcept) o;

        if (!uuid.equals(that.uuid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public String getConceptClass() {
        return conceptClass;
    }

    public void setConceptClass(String conceptClass) {
        this.conceptClass = conceptClass;
    }
}
