package test.demoyamlspringbatch.batch.impl.reader;

import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemStream;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Iterator;
import java.util.Map;

public class NamedParameterJdbcItemReader<T>
        implements ItemReader<T> , ItemStream {

    private final NamedParameterJdbcTemplate template;
    private final String sql;
    private final Map<String, Object> params;
    private final RowMapper<T> rowMapper;

    private Iterator<T> iterator;

    public NamedParameterJdbcItemReader(
            NamedParameterJdbcTemplate template,
            String sql,
            Map<String, Object> params,
            RowMapper<T> rowMapper
    ) {
        this.template = template;
        this.sql = sql;
        this.params = params;
        this.rowMapper = rowMapper;
    }
    @Override
    public void open(ExecutionContext executionContext) {
        iterator = template.query(sql, params, rowMapper).iterator();
    }

    @Override
    public T read() {
        if (iterator == null) return null;
        return iterator.hasNext() ? iterator.next() : null;
    }
}