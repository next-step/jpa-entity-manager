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
        return (RowMapper<T>) rowMappers.get(clazz);
    }
}
