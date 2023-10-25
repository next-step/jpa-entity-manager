package persistence.entity.impl.store;

import persistence.sql.dialect.ColumnType;

public interface EntityPersister {

    boolean update(Object entity, ColumnType columnType);
    void store(Object entity, ColumnType columnType);

    void delete(Object entity, ColumnType columnType);
}
