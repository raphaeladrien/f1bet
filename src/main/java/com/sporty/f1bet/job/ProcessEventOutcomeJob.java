package com.sporty.f1bet.job;

import com.sporty.f1bet.interactors.ProcessEventOutcome;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcessEventOutcomeJob {

    private final ProcessEventOutcome processEventOutcome;

    public ProcessEventOutcomeJob(ProcessEventOutcome processEventOutcome) {
        this.processEventOutcome = processEventOutcome;
    }

    @Scheduled(cron = "*/15 * * * * *")
    public void run() {
        Integer processedBets = processEventOutcome.processOutcome();
        System.out.println(processedBets);
    }
}
