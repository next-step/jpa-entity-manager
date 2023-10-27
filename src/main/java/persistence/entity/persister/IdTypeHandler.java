package persistence.entity.persister;

import java.lang.reflect.Field;

interface IdTypeHandler {
    boolean support(Class<?> idType);

    <T> void setGeneratedIdToEntity(T instance, Field idField, long key);
}
