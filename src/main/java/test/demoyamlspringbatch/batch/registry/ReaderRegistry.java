package test.demoyamlspringbatch.batch.registry;

import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReaderRegistry {

    private final ApplicationContext context;

    public ReaderRegistry(ApplicationContext context) {
        this.context = context;
    }
    @SuppressWarnings("unchecked")
    public <I>ItemReader<I> get(String beanName) {
        return (ItemReader<I>) context.getBean(beanName, ItemReader.class);
    }
}