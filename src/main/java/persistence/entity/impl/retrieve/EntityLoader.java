package persistence.entity.impl.retrieve;

import persistence.sql.dialect.ColumnType;

public interface EntityLoader {

    <T> T load(Class<T> clazz, Object id, ColumnType columnType);
}
