package test.demoyamlspringbatch.batch.impl.processor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class LowerCaseProcessor implements ItemProcessor<String ,String> {
    @Override
    public String process(String item) {
        return item.toLowerCase();
    }
}
