package test.demoyamlspringbatch.batch.impl.processor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.model.entites.InputData;

@Component
@StepScope
public class InputProcessor implements ItemProcessor<InputData, String> {

    @Override
    public String process(InputData item) {
        return item.name().toUpperCase();
    }
}