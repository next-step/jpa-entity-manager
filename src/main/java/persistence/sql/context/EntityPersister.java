package persistence.sql.context;

import persistence.sql.dml.MetadataLoader;

import java.util.List;

public interface EntityPersister {

    <T> Object insert(T entity, MetadataLoader<?> loader);

    <T> boolean update(T entity, MetadataLoader<?> loader);

    <T> boolean delete(T entity, MetadataLoader<?> loader);

    <T> T select(Class<T> entityType, Object primaryKey, MetadataLoader<?> loader);

    <T> List<T> selectAll(Class<T> entityType, MetadataLoader<?> loader);
}
