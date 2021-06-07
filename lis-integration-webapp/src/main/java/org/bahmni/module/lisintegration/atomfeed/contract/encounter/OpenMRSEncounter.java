package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import org.bahmni.module.lisintegration.model.OrderType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSEncounter {
    private String encounterUuid;
    private String patientUuid;
    private List<OpenMRSOrder> orders = new ArrayList<OpenMRSOrder>();
    private List<OpenMRSObs> obs = new ArrayList<OpenMRSObs>();
    private List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();
    private String encounterType;
    private String encounterRole;
    private OpenMRSVisit visit;
    private PatientDocument patientDocument;
    private boolean panel;

    public boolean isPanel() {
        return this.panel;
    }

    public void setPanel(boolean panel) {
        this.panel = panel;
    }

    public PatientDocument getPatientDocument() {
        return this.patientDocument;
    }

    public void setPatientDocument(PatientDocument patientDocument) {
        this.patientDocument = patientDocument;
    }

    public OpenMRSEncounter() {
    }

    public OpenMRSEncounter(final String encounterUuid, final String patientUuid, final List<OpenMRSOrder> orders,
            final List<OpenMRSProvider> providers) {

        this.encounterUuid = encounterUuid;
        this.orders = orders;
        this.patientUuid = patientUuid;
        this.providers = providers;
    }

    public void setEncounterUuid(String encounterUuid) {
        this.encounterUuid = encounterUuid;
    }

    public String getEncounterUuid() {
        return encounterUuid;
    }

    public List<OpenMRSOrder> getOrders() {
        return orders;
    }

    public List<OpenMRSOrder> getAcceptableTestOrders(List<OrderType> acceptableOrderTypes) {
        List<OpenMRSOrder> acceptableNewOrders = new ArrayList<OpenMRSOrder>();
        for (OpenMRSOrder openMRSOrder : this.orders) {
            OrderType acceptableOrderType = findOrderType(acceptableOrderTypes, openMRSOrder.getOrderType());
            if (acceptableOrderType != null) {
                acceptableNewOrders.add(openMRSOrder);
            }
        }
        return acceptableNewOrders;
    }

    public void setOrders(List<OpenMRSOrder> orders) {
        this.orders = orders;
    }

    public String getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(String patientUuid) {
        this.patientUuid = patientUuid;
    }

    public List<OpenMRSProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<OpenMRSProvider> providers) {
        this.providers = providers;
    }

    public List<OpenMRSObs> getObs() {
        return obs;
    }

    public void setObs(List<OpenMRSObs> obs) {
        this.obs = obs;
    }

    public void addTestOrder(OpenMRSOrder order) {
        orders.add(order);
    }

    private OrderType findOrderType(List<OrderType> acceptableOrderTypes, String orderType) {
        for (OrderType acceptableOrderType : acceptableOrderTypes) {
            if (acceptableOrderType.getName().equals(orderType)) {
                return acceptableOrderType;
            }
        }
        return null;
    }

    public boolean hasOrders() {
        return getOrders().size() > 0;
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

    public OpenMRSVisit getVisit() {
        return visit;
    }

    public void setVisit(OpenMRSVisit visit) {
        this.visit = visit;
    }
}
