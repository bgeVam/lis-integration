package org.bahmni.module.lisintegration.atomfeed.jobs;

import org.bahmni.module.lisintegration.atomfeed.client.AtomFeedClientFactory;
import org.bahmni.module.lisintegration.atomfeed.worker.EncounterFeedWorker;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterFeedJobTest {
    private static final String OPENMRS_ENCOUNTER_FEED_NAME = "openmrs.encounter.feed.uri";

    @Mock
    EncounterFeedWorker encounterFeedWorker;

    @Mock
    AtomFeedClientFactory atomFeedClientFactory;

    @Mock
    AtomFeedClient atomFeedClient;

    @InjectMocks
    private EncounterFeedJob encounterFeedJob = new EncounterFeedJob();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldProcessTheEvent() throws Exception {
        when(atomFeedClientFactory.get(OPENMRS_ENCOUNTER_FEED_NAME, encounterFeedWorker)).thenReturn(atomFeedClient);

        encounterFeedJob.process();

        verify(atomFeedClient, times(1)).processEvents();
    }
}