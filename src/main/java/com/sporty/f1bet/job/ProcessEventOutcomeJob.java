package com.sporty.f1bet.job;

import com.sporty.f1bet.interactors.ProcessEventOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessEventOutcomeJob {

    private final ProcessEventOutcome processEventOutcome;

    private static final Logger logger = LoggerFactory.getLogger(ProcessEventOutcomeJob.class);

    public ProcessEventOutcomeJob(ProcessEventOutcome processEventOutcome) {
        this.processEventOutcome = processEventOutcome;
    }

    @Scheduled(cron = "${EVENT_OUTCOME_CRON}")
    public void run() {
        try {
            Integer processedBets = processEventOutcome.processOutcome();
            if (processedBets == 0) {
                logger.info("No pending events or bets to process in this run.");
            } else {
                logger.info("Successfully processed {} bet(s) in this run.", processedBets);
            }
        } catch (Exception ex) {
            logger.error("Error occurred while processing event outcomes.", ex);
        }
    }
}
