package com.mysite.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        immediate = true,
        service = JobConsumer.class,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=project/task/slingjob"
        }
)
public class SlingJob implements JobConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlingJob.class);
    @Override
    public JobResult process(Job job) {
        try {
            String topic = job.getTopic();
            LOGGER.info("Sending XML from JCR to SFTP is Running " + topic);
            // Task which we want to perform for sling job.
        }catch (Exception e){
            LOGGER.error("Error while scheduling the SFTP Job"+e);
        }

        return JobResult.OK;
    }
}