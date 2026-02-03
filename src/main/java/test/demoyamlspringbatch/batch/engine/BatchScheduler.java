package test.demoyamlspringbatch.batch.engine;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.model.JobDefinition;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {
    private final YamlJobLoader loader;
    private final YamlJobFactory factory;
    private final JobOperator jobLauncher;
    private final TaskScheduler taskScheduler;
    private final Map<String, Job> jobMap = new HashMap<>();
    @PostConstruct
    public void init() throws Exception {
        List<JobDefinition> defs =
                loader.loadAll("batch-jobs/demo-job.yml");

        for (JobDefinition def : defs) {
            Job job = factory.build(def);   // ✅ build 1 lần
            jobMap.put(def.getName(), job);
            schedule(def);
        }
    }

    private void schedule(JobDefinition def) {

        Runnable task = () -> {
            try {
                Job job = jobMap.get(def.getName());

                jobLauncher.start(job, buildParams(def));

            } catch (Exception e) {
                log.error("Job failed: {}", def.getName(), e);
            }
        };

        if ("cron".equals(def.getSchedule().getType())) {
            taskScheduler.schedule(
                    task,
                    new CronTrigger(def.getSchedule().getExpression())
            );
        }
    }
    private JobParameters buildParams(JobDefinition def) {

        JobParametersBuilder builder = new JobParametersBuilder();

        def.getParameters().forEach((k, v) -> {
            if (v instanceof String s) {
                builder.addString(k, s);
            } else if (v instanceof Number n) {
                builder.addLong(k, n.longValue());
            } else if (v instanceof LocalDate d) {
                builder.addString(k, d.toString());
            }
        });
        builder.addString("run.id",
                String.valueOf(System.currentTimeMillis()));
        return builder.toJobParameters();
    }
}
