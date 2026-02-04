package test.demoyamlspringbatch.batch.registry;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RowMapperRegistry {

    private final Map<String, RowMapper<?>> map = new HashMap<>();

    public RowMapperRegistry(List<RowMapper<?>> rowMappers) {
        rowMappers.forEach(rm -> {
            String key = lowerFirst(rm.getClass().getSimpleName());
            map.put(key, rm);
        });
    }

    public RowMapper<?> get(String name) {
        RowMapper<?> rm = map.get(name);
        if (rm == null) {
            throw new IllegalArgumentException("RowMapper not found: " + name);
        }
        return rm;
    }

    private String lowerFirst(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}