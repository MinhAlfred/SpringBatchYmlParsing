package test.demoyamlspringbatch.batch.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.model.processor.BeanProcessorDefinition;
import test.demoyamlspringbatch.batch.model.processor.ProcessorDefinition;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessorFactory {
    private final ApplicationContext context;

    @SuppressWarnings("unchecked")
    public <I, O> ItemProcessor<I, O> build(ProcessorDefinition def) {
        if (def instanceof BeanProcessorDefinition beanDef) {
            ItemProcessor<I, O> processor = context.getBean(beanDef.getBean(), ItemProcessor.class);

            ResolvableType type = ResolvableType
                    .forClass(processor.getClass())
                    .as(ItemProcessor.class);

            Class<?> inputType  = type.getGeneric(0).resolve();
            Class<?> outputType = type.getGeneric(1).resolve();

            if (inputType == null || outputType == null) {
                throw new IllegalStateException(
                        "Cannot resolve generic types for processor: " + beanDef.getBean()
                                + " — đảm bảo class không dùng raw type"
                );
            }

            log.debug("Processor [{}] — input: {}, output: {}",
                    beanDef.getBean(), inputType.getSimpleName(), outputType.getSimpleName());

            return processor;
        }

        throw new IllegalArgumentException(
                "Unsupported processor definition: " + def.getClass()
        );
    }

    public Class<?> resolveOutputType(ProcessorDefinition def) {
        if (def instanceof BeanProcessorDefinition beanDef) {
            Class<?> type = ResolvableType
                    .forClass(context.getBean(beanDef.getBean(), ItemProcessor.class).getClass())
                    .as(ItemProcessor.class)
                    .getGeneric(1)
                    .resolve();

            if (type == null) {
                throw new IllegalStateException(
                        "Cannot resolve output type for processor: " + beanDef.getBean()
                );
            }
            return type;
        }
        throw new IllegalArgumentException("Unsupported processor definition: " + def.getClass());
    }

    public void validateChain(ProcessorDefinition def, Class<?> readerOutputType) {
        if (def instanceof BeanProcessorDefinition beanDef) {
            ResolvableType type = ResolvableType
                    .forClass(context.getBean(beanDef.getBean(), ItemProcessor.class).getClass())
                    .as(ItemProcessor.class);

            Class<?> inputType = type.getGeneric(0).resolve();

            if (inputType == null) {
                throw new IllegalStateException(
                        "Cannot resolve input type for processor: " + beanDef.getBean()
                );
            }

            if (!inputType.isAssignableFrom(readerOutputType)) {
                throw new IllegalStateException(
                        "Type mismatch — Reader output: [" + readerOutputType.getSimpleName()
                                + "] not compatible with Processor input: [" + inputType.getSimpleName() + "]"
                );
            }
            return;
        }

        throw new IllegalArgumentException(
                "Unsupported processor definition: " + def.getClass()
        );
    }
}
