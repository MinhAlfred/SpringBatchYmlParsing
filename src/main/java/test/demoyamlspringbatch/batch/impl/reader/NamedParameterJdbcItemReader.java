package test.demoyamlspringbatch.batch.impl.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NamedParameterJdbcItemReader<T>
        implements ItemReader<T> {

    private final Iterator<T> iterator;

    public NamedParameterJdbcItemReader(
            NamedParameterJdbcTemplate template,
            String sql,
            Map<String, Object> params,
            RowMapper<T> rowMapper
    ) {
        List<T> result = template.query(sql, params, rowMapper);
        this.iterator = result.iterator();
    }

    @Override
    public T read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}