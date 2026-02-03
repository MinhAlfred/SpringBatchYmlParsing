package test.demoyamlspringbatch.batch.impl.tasklet;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CleanTempFileTasklet implements Tasklet {

    @Value("#{jobParameters['path']}")
    private String path;

    @Override
    public RepeatStatus execute(StepContribution c, ChunkContext ctx) {
        System.out.println("Cleaning: " + path);
        return RepeatStatus.FINISHED;
    }
}