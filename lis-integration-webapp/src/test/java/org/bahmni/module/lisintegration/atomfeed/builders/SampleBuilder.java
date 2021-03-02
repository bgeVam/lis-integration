package org.bahmni.module.lisintegration.atomfeed.builders;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;

public class SampleBuilder {
    private Sample sample;

    public SampleBuilder() {
        sample = new Sample();
    }

    public SampleBuilder withDisplay(String display) {
        sample.setName(display);
        return this;
    }

    public Sample build() {
        return sample;
    }
}
