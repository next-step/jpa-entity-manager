package persistence.context;

import persistence.entity.attribute.id.IdAttribute;

import java.lang.reflect.Field;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, String id);

    <T> void removeEntity(T instance, Field idField);

    <T> T addEntity(T instance, IdAttribute idAttribute);

    <T> T getDatabaseSnapshot(String id, T entity);
}
