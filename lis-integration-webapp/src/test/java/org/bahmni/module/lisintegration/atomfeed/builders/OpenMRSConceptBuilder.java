package org.bahmni.module.lisintegration.atomfeed.builders;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptMapping;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptName;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConceptClass;

import java.util.ArrayList;
import java.util.List;

public class OpenMRSConceptBuilder {
    private OpenMRSConcept openMRSConcept;

    public OpenMRSConceptBuilder() {
        this.openMRSConcept = new OpenMRSConcept();
    }

    public OpenMRSConceptBuilder withUuid(String uuid) {
        openMRSConcept.setUuid(uuid);
        return this;
    }

    public OpenMRSConceptBuilder withName(OpenMRSConceptName conceptName) {
        openMRSConcept.setName(conceptName);
        return this;
    }

    public OpenMRSConceptBuilder withConceptMappings(List<OpenMRSConceptMapping> conceptMappings) {
        openMRSConcept.setMappings(conceptMappings);
        return this;
    }

    public OpenMRSConceptBuilder addConceptMapping(OpenMRSConceptMapping mapping) {
        if(openMRSConcept.getMappings() == null) {
            openMRSConcept.setMappings(new ArrayList<OpenMRSConceptMapping>());
        }
        openMRSConcept.getMappings().add(mapping);
        return this;
    }

    public OpenMRSConcept build() {
        return openMRSConcept;
    }

    public OpenMRSConceptBuilder addConceptName(String lisCode) {
        openMRSConcept.setName(new OpenMRSConceptName(lisCode));
        return this;
    }

    public OpenMRSConceptBuilder addConceptClass(String conceptClass) {
        openMRSConcept.setConceptClass(conceptClass);
        return this;
    }
}
