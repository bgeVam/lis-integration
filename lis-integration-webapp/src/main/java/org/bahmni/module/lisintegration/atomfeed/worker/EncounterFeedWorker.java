
package org.bahmni.module.lisintegration.atomfeed.worker;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.services.OpenMRSService;
import org.bahmni.module.lisintegration.services.LisIntegrationService;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterFeedWorker implements EventWorker {

    private static final Logger logger = LoggerFactory.getLogger(EncounterFeedWorker.class);

    @Autowired
    private LisIntegrationService lisIntegrationService;

    @Autowired
    private OpenMRSService openMRSService;

    public EncounterFeedWorker() {
    }

    @Override
    public void process(Event event) {
        String bedAssignment = "Bed-Assignment";
        try {
            if(event.getTitle() == null || !event.getTitle().equals(bedAssignment)) {
                logger.info("Getting encounter data...");
                String encounterUri = event.getContent();
                OpenMRSEncounter encounter = openMRSService.getEncounter(encounterUri);
                if(encounter.hasOrders()) {
                    lisIntegrationService.processEncounter(encounter);
                }
            }
        } catch (Exception e) {
            logger.error("Failed send order to LIS", e);
            throw new RuntimeException("Failed send order to LIS", e);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }
}
