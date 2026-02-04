package test.demoyamlspringbatch.batch.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import test.demoyamlspringbatch.batch.factory.ReaderFactory;
import test.demoyamlspringbatch.batch.model.ChunkStepDefinition;
import test.demoyamlspringbatch.batch.model.JobDefinition;
import test.demoyamlspringbatch.batch.model.StepDefinition;
import test.demoyamlspringbatch.batch.model.TaskletStepDefinition;
import test.demoyamlspringbatch.batch.registry.ProcessorRegistry;
import test.demoyamlspringbatch.batch.registry.ReaderRegistry;
import test.demoyamlspringbatch.batch.registry.TaskletRegistry;
import test.demoyamlspringbatch.batch.registry.WriterRegistry;

@Component
@RequiredArgsConstructor
public class YamlJobFactory {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ReaderFactory readerFactory;
    private final ProcessorRegistry processorRegistry;
    private final WriterRegistry writerRegistry;
    private final TaskletRegistry taskletRegistry;
    public Job build(JobDefinition def, JobParameters params) {

        JobBuilder jobBuilder = new JobBuilder(def.getName(), jobRepository);

        FlowBuilder<Flow> flow = new FlowBuilder<>("flow");

        for (StepDefinition stepDef : def.getSteps()) {
            flow.next(buildStep(stepDef, params));
        }

        return jobBuilder.start(flow.build()).end().build();
    }
    public Step buildStep(StepDefinition def,JobParameters jobParameters) {

        if (def instanceof TaskletStepDefinition taskletDef) {
            return buildTaskletStep(taskletDef);
        }

        if (def instanceof ChunkStepDefinition chunkDef) {
            return buildChunkStep(chunkDef,jobParameters);
        }

        throw new IllegalArgumentException("Unknown step type");
    }
    private Step buildChunkStep(
            ChunkStepDefinition def,
            JobParameters jobParameters
    ) {
        ItemReader<Object> reader =
                (ItemReader<Object>) readerFactory.build(def.getReader(), jobParameters);

        ItemProcessor<Object, Object> processor =
                        processorRegistry.get(def.getProcessor().getBean());

        ItemWriter<Object> writer =
                        writerRegistry.get(def.getWriter().getBean());

        return new StepBuilder(def.getName(), jobRepository)
                .<Object, Object>chunk(def.getChunkSize())
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    private Step buildTaskletStep(TaskletStepDefinition def) {

        Tasklet tasklet =
                taskletRegistry.get(def.getTasklet());

        return new StepBuilder(def.getName(),jobRepository)
                .tasklet(tasklet)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        if (def.getParams() != null) {
                            def.getParams().forEach(
                                    (k, v) -> stepExecution
                                            .getExecutionContext()
                                            .put(k, v)
                            );
                        }
                    }
                })
                .build();
    }
}