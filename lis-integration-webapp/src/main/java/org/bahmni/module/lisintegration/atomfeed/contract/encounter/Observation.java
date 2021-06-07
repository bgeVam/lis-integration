package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Observation {
    private String obsDatetime;
    private String order;
    private String concept;
    private List<Observation> groupMembers = new ArrayList<Observation>();
    private Double value;
    private String valueText;

    public  Observation() {

    }

    public Observation(final String obsDateTime, final String concept, final String order) {
        this.obsDatetime = obsDateTime;
        this.concept = concept;
        this.order = order;
    }

    public void setGroupMembers(List<Observation> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getObsDatetime() {
        return obsDatetime;
    }

    public void setObsDatetime(String obsDateTime) {
        this.obsDatetime = obsDateTime;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    @JsonInclude(Include.NON_NULL)
    public List<Observation> getGroupMembers() {
        return groupMembers.isEmpty() ? null : groupMembers;
    }

    @JsonInclude(Include.NON_NULL)
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getValueText() {
        return this.valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }
}
