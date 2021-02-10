package org.bahmni.module.lisintegration.atomfeed.jobs;

public interface FeedJob {
    void process() throws InterruptedException;
}
