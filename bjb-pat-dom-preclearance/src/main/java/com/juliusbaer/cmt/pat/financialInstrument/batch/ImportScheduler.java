package com.juliusbaer.cmt.pat.financialInstrument.batch;


import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.juliusbaer.cmt.pat.financialInstrument.config.CsvProperties;

@Component
@RequiredArgsConstructor
public class ImportScheduler {

    private final JobLauncher jobLauncher;
    private final Job importJob;
    private final CsvProperties csvProperties;

    @Scheduled(cron = "#{@csvProperties.cronExpression}")
    @ConditionalOnProperty(
            value = "bjb.pat.financialinstrument.job.scheduled-enabled",
            havingValue = "true",
            matchIfMissing = true)
    @SchedulerLock( // <-- YOU ADD THIS ANNOTATION
            name = "importFinancialInstrumentsJob_lock",
            lockAtMostFor = "1h",
            lockAtLeastFor = "1m")
    public void runScheduled() throws Exception {
        launch();
    }

    // manual trigger (optional)
    public void launch() throws Exception {
        // Use millisecond precision to ensure unique run.id parameter
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(importJob, params);
    }
}