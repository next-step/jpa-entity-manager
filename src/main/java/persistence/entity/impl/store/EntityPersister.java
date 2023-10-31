package persistence.entity.impl.store;

import persistence.sql.dialect.ColumnType;

public interface EntityPersister {

    void update(Object entity, ColumnType columnType);
    Object store(Object entity, ColumnType columnType);

    void delete(Object entity, ColumnType columnType);
}
