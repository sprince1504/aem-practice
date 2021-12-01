package com.mysite.core.events.handler;

import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SlingEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlingEvent.class);
    @Reference
    private JobManager jobManager;

    @Activate
    public void startScheduledJob() {
        LOGGER.info("Bazaarvoice Send XML to SFTP Job has been started.");
        JobBuilder.ScheduleBuilder scheduleBuilder = jobManager.createJob("project/task/slingjob").schedule();
        // Schedule a job to perform a task
        if (scheduleBuilder.add() == null) {
            LOGGER.error("Error sending the file from AEM to SFTP Server job has failed.");
        }
    }
}