package test.demoyamlspringbatch.batch.registry;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class WriterRegistry {
    private final ApplicationContext applicationContext;

    public ItemWriter<?> get(String beanName) {
        return applicationContext.getBean(beanName, ItemWriter.class);
    }
}
