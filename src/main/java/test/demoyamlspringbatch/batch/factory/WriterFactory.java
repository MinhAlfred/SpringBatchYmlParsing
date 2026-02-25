package test.demoyamlspringbatch.batch.registry;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class WriterFactory {
    private final ApplicationContext context;

    @SuppressWarnings("unchecked")
    public <O> ItemWriter<O> build(WriterDefinition def) {
        if (def instanceof BeanWriterDefinition beanDef) {
            ItemWriter<O> writer = context.getBean(beanDef.getBean(), ItemWriter.class);
            return writer;
        }

        throw new IllegalArgumentException(
                "Unsupported writer definition: " + def.getClass()
        );
    }

    public Class<?> resolveInputType(WriterDefinition def) {
        if (def instanceof BeanWriterDefinition beanDef) {
            Class<?> type = ResolvableType
                    .forClass(context.getBean(beanDef.getBean(), ItemWriter.class).getClass())
                    .as(ItemWriter.class)
                    .getGeneric(0)
                    .resolve();

            if (type == null) {
                throw new IllegalStateException(
                        "Cannot resolve input type for Writer: " + beanDef.getBean()
                                + " — đảm bảo không dùng lambda hoặc raw type"
                );
            }
            return type;
        }

        throw new IllegalArgumentException(
                "Unsupported writer definition: " + def.getClass()
        );
    }
}
