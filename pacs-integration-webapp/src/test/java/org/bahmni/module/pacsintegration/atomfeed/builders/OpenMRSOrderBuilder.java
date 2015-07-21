package org.bahmni.module.pacsintegration.atomfeed.builders;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;

public class OpenMRSOrderBuilder {
    private OpenMRSOrder openMRSOrder;

    public OpenMRSOrderBuilder() {
        this.openMRSOrder = new OpenMRSOrder();
    }

    public OpenMRSOrderBuilder withOrderUuid(String orderUuid) {
        openMRSOrder.setUuid(orderUuid);
        return this;
    }

    public OpenMRSOrderBuilder withOrderType(String orderType) {
        openMRSOrder.setOrderType(orderType);
        return this;
    }

    public OpenMRSOrderBuilder withVoided(boolean voided) {
        openMRSOrder.setVoided(voided);
        return this;
    }

    public OpenMRSOrderBuilder withConcept(OpenMRSConcept concept) {
        openMRSOrder.setConcept(concept);
        return this;
    }

    public OpenMRSOrderBuilder withPreviousOrderUuid(String uuid) {
        openMRSOrder.setPreviousOrderUuid(uuid);
        return this;
    }

    public OpenMRSOrder build() {
        return openMRSOrder;
    }

    public OpenMRSOrderBuilder withOrderNumber(String orderNumber) {
        openMRSOrder.setOrderNumber(orderNumber);
        return this;
    }

    public OpenMRSOrderBuilder withDiscontinued(){
        openMRSOrder.setAction(openMRSOrder.ACTION_DISCONTINUE);
        return this;
    }


}
