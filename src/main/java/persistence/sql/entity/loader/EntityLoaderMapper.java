package persistence.sql.entity.loader;

import persistence.sql.dml.exception.*;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.DomainType;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class EntityLoaderMapper {

    private EntityLoaderMapper() {}

    private static class EntityLoaderMapperSingleton {
        private static final EntityLoaderMapper ENTITY_LOADER_MAPPER = new EntityLoaderMapper();
    }

    public static EntityLoaderMapper getInstance() {
        return EntityLoaderMapperSingleton.ENTITY_LOADER_MAPPER;
    }

    public <T> T mapper(Class<T> clazz, ResultSet resultSet) {
        EntityMappingTable entityMappingTable = EntityMappingTable.from(clazz);
        T instance = createInstance(clazz);

        Spliterator<DomainType> spliterator = entityMappingTable.getDomainTypes().spliterator();
        StreamSupport.stream(spliterator, false)
                .forEach(domainType -> {
                    Field field = getField(clazz, domainType.getName());
                    setField(instance, field, getValue(resultSet, domainType.getColumnName()));
                });

        return instance;
    }

    private <T> T createInstance(final Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new InstanceException();
        }
    }

    private Field getField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (Exception e) {
            throw new NotFoundFieldException();
        }
    }

    private Object getValue(ResultSet resultSet, String columnName) {
        try {
            return resultSet.getObject(columnName);
        } catch (Exception e) {
            throw new InvalidFieldValueException();
        }
    }

    private void setField(Object instance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
            throw new FieldSetValueException();
        }
    }
}
