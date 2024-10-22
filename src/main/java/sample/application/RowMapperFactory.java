package sample.application;

import jdbc.RowMapper;

import java.util.HashMap;
import java.util.Map;

public record RowMapperFactory(Map<Class<?>, RowMapper<?>> rowMappers) {
    public RowMapperFactory() {
        this(new HashMap<>());
    }

    public <T> RowMapperFactory addRowMapper(Class<T> clazz, RowMapper<T> rowMapper) {
        rowMappers.put(clazz, rowMapper);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> getRowMapper(Class<T> clazz) {
        RowMapper<?> foundRowMapper = rowMappers.get(clazz);

        if (foundRowMapper == null) {
            throw new IllegalArgumentException("RowMapper not found for class: " + clazz.getName());
        }

        return (RowMapper<T>) foundRowMapper;
    }
}
