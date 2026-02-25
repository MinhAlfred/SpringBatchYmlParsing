package test.demoyamlspringbatch.batch.registry;

import org.springframework.core.ResolvableType;
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

    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> get(String name) {
        return (RowMapper<T>) getOrThrow(name);
    }

    public Class<?> resolveType(String name) {
        Class<?> type = ResolvableType
                .forClass(getOrThrow(name).getClass())
                .as(RowMapper.class)
                .getGeneric(0)
                .resolve();

        if (type == null) {
            throw new IllegalStateException(
                    "Cannot resolve generic type for RowMapper: " + name
                            + " — đảm bảo không dùng lambda hoặc raw type"
            );
        }
        return type;
    }

    private RowMapper<?> getOrThrow(String name) {
        RowMapper<?> rm = map.get(name);
        if (rm == null) {
            throw new IllegalArgumentException(
                    "RowMapper not found: " + name
                            + " — available: " + map.keySet()
            );
        }
        return rm;
    }

    private String lowerFirst(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}