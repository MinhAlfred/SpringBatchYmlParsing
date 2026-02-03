package test.demoyamlspringbatch.batch.registry;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProcessorRegistry {

    private final ApplicationContext context;

    public ProcessorRegistry(ApplicationContext context) {
        this.context = context;
    }

    public ItemProcessor<?, ?> get(String name) {
        return context.getBean(name, ItemProcessor.class);
    }

}
