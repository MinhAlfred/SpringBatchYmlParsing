package test.demoyamlspringbatch.batch.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.impl.writer.NamedParameterJdbcItemWriter;
import test.demoyamlspringbatch.batch.model.writer.BeanWriterDefinition;
import test.demoyamlspringbatch.batch.model.writer.JdbcWriterDefinition;
import test.demoyamlspringbatch.batch.model.writer.WriterDefinition;

import javax.sql.DataSource;
import java.util.List;


@Component
@RequiredArgsConstructor
public class WriterFactory {
    private final ApplicationContext context;
    private final DataSource dataSource;

    @SuppressWarnings("unchecked")
    public <O> ItemWriter<O> build(WriterDefinition def) {
        if (def instanceof BeanWriterDefinition beanDef) {
            @SuppressWarnings("unchecked")
            ItemWriter<O> writer = context.getBean(beanDef.getBean(), ItemWriter.class);
            return writer;
        }
        if (def instanceof JdbcWriterDefinition jdbcDef) {
            return buildJdbcWriter(jdbcDef);
        }

        throw new IllegalArgumentException(
                "Unsupported writer definition: " + def.getClass()
        );
    }

    private <O> ItemWriter<O> buildJdbcWriter(JdbcWriterDefinition def) {
        NamedParameterJdbcTemplate template =
                new NamedParameterJdbcTemplate(dataSource);

        return new NamedParameterJdbcItemWriter<>(template, def.getSql());
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

    public void validateChain(WriterDefinition def, Class<?> processorOutputType) {
        if (def instanceof BeanWriterDefinition beanDef) {
            Class<?> inputType = ResolvableType
                    .forClass(context.getBean(beanDef.getBean(), ItemWriter.class).getClass())
                    .as(ItemWriter.class)
                    .getGeneric(0)
                    .resolve();

            if (inputType == null) {
                throw new IllegalStateException(
                        "Cannot resolve input type for writer: " + beanDef.getBean()
                );
            }

            if (!inputType.isAssignableFrom(processorOutputType)) {
                throw new IllegalStateException(
                        "Type mismatch — Processor output: [" + processorOutputType.getSimpleName()
                                + "] not compatible with Writer input: [" + inputType.getSimpleName() + "]"
                );
            }
            return;
        }
        if (def instanceof JdbcWriterDefinition) {
            return;
        }
        throw new IllegalArgumentException("Unsupported writer definition: " + def.getClass());
    }
}
