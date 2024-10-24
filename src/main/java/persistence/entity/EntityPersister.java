package persistence.entity;

import persistence.sql.meta.EntityColumn;

import java.util.List;

public interface EntityPersister {
    void insert(Object entity);

    void update(Object entity, List<EntityColumn> entityColumns);

    void delete(Object entity);
}
