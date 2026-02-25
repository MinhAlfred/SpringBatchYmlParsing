package test.demoyamlspringbatch.batch.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import test.demoyamlspringbatch.batch.factory.ProcessorFactory;
import test.demoyamlspringbatch.batch.factory.ReaderFactory;
import test.demoyamlspringbatch.batch.model.ChunkStepDefinition;
import test.demoyamlspringbatch.batch.model.JobDefinition;
import test.demoyamlspringbatch.batch.model.StepDefinition;
import test.demoyamlspringbatch.batch.model.TaskletStepDefinition;
import test.demoyamlspringbatch.batch.model.processor.ProcessorDefinition;
import test.demoyamlspringbatch.batch.model.reader.ReaderDefinition;
import test.demoyamlspringbatch.batch.registry.TaskletRegistry;
import test.demoyamlspringbatch.batch.factory.WriterFactory;

@Component
@RequiredArgsConstructor
public class YamlJobFactory {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ReaderFactory readerFactory;
    private final ProcessorFactory processorFactory;
    private final WriterFactory writerFactory;
    private final TaskletRegistry taskletRegistry;
    public Job build(JobDefinition def, JobParameters params) {

        JobBuilder jobBuilder = new JobBuilder(def.getName(), jobRepository);

        FlowBuilder<Flow> flow = new FlowBuilder<>(def.getName() + "-flow");

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
        ReaderDefinition readerDef = def.getReader();
        ItemReader<Object> reader = readerFactory.build(readerDef, jobParameters);
        Class<?> readerOutputType = readerFactory.resolveOutputType(readerDef);

        ItemProcessor<Object, Object> processor = null;
        Class<?> processorOutputType = readerOutputType;
        if (def.getProcessor() != null) {
            ProcessorDefinition processorBean = def.getProcessor();
            processor = processorFactory.build(processorBean);
            processorFactory.validateChain(processorBean, readerOutputType);
            processorOutputType = processorFactory.resolveOutputType(processorBean);
        }

        writerFactory.validateChain(def.getWriter(), processorOutputType);
        ItemWriter<Object> writer = writerFactory.build(def.getWriter());

        return new StepBuilder(def.getName(), jobRepository)
                .chunk(def.getChunkSize())
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