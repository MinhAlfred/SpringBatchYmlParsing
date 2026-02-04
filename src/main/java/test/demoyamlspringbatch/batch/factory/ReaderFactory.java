package test.demoyamlspringbatch.batch.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import test.demoyamlspringbatch.batch.impl.reader.NamedParameterJdbcItemReader;
import test.demoyamlspringbatch.batch.model.reader.BeanReaderDefinition;
import test.demoyamlspringbatch.batch.model.reader.JdbcReaderDefinition;
import test.demoyamlspringbatch.batch.model.reader.ReaderDefinition;
import test.demoyamlspringbatch.batch.registry.RowMapperRegistry;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ReaderFactory {

    private final ApplicationContext context;
    private final DataSource dataSource;
    private final RowMapperRegistry rowMapperRegistry;

    public ReaderFactory(ApplicationContext context,
                         DataSource dataSource,
                         RowMapperRegistry rowMapperRegistry) {
        this.context = context;
        this.dataSource = dataSource;
        this.rowMapperRegistry = rowMapperRegistry;
    }

    public ItemReader<?> build(
            ReaderDefinition def,
            JobParameters jobParameters
    ) {
        if (def instanceof BeanReaderDefinition beanDef) {
            return context.getBean(
                    beanDef.getBean(),
                    ItemReader.class
            );
        }
        if (def instanceof JdbcReaderDefinition jdbcDef) {
            return buildJdbcReader(jdbcDef, jobParameters);
        }

        throw new IllegalArgumentException(
                "Unsupported reader definition: " + def.getClass()
        );
    }

        private ItemReader<?> buildJdbcReader(
            JdbcReaderDefinition def,
            JobParameters jobParameters
    ) {

        NamedParameterJdbcTemplate template =
                new NamedParameterJdbcTemplate(dataSource);

        Map<String, Object> params =
                mergeParams(def.getParams(), jobParameters);
        // 🔥 log này cực quan trọng để debug
        log.info("JDBC reader params = {}", params);
        return new NamedParameterJdbcItemReader<>(
                template,
                def.getSql(),
                params,
                rowMapperRegistry.get(def.getRowMapper())
        );
    }

    private Map<String, Object> mergeParams(
            Map<String, Object> readerParams,
            JobParameters jobParameters
    ) {
        Map<String, Object> map = new HashMap<>();

        // 1️⃣ params khai báo trong reader (nếu có)
        if (readerParams != null) {
            map.putAll(readerParams);
        }

        // 2️⃣ params từ job.parameters (YAML)
        jobParameters.parameters().forEach(p ->
                map.putIfAbsent(p.name(), p.value())
        );

        return map;
    }
}