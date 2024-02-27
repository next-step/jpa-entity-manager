package persistence.entity;

import persistence.sql.column.IdColumn;

public interface EntityPersister {

    boolean update(Object entity);

    void insert(Object entity);

    void delete(Object entity, IdColumn idColumn);
}
