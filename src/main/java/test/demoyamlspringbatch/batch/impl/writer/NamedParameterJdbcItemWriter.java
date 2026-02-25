package test.demoyamlspringbatch.batch.impl.writer;

import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

public class NamedParameterJdbcItemWriter<O> implements ItemWriter<O> {

    private final NamedParameterJdbcTemplate template;
    private final String sql;

    public NamedParameterJdbcItemWriter(
            NamedParameterJdbcTemplate template,
            String sql
    ) {
        this.template = template;
        this.sql = sql;
    }

    @Override
    public void write(Chunk<? extends O> chunk) {
        List<SqlParameterSource> params = chunk.getItems().stream()
                .map(item -> (SqlParameterSource) new BeanPropertySqlParameterSource(item))
                .toList();

        template.batchUpdate(sql, params.toArray(new SqlParameterSource[0]));
    }
}