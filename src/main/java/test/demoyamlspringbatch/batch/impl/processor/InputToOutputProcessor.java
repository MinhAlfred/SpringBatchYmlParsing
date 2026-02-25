package test.demoyamlspringbatch.batch.impl.processor;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.model.entites.InputData;
import test.demoyamlspringbatch.batch.model.entites.OutputData;

import java.time.LocalDateTime;

@Component
@StepScope
public class InputToOutputProcessor implements ItemProcessor<InputData, OutputData> {
    @Override
    public @Nullable OutputData process(InputData item) {
        return new OutputData(item.name(), LocalDateTime.now());
    }
}
