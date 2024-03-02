package database.mapping;

import database.dialect.MySQLDialect;
import jdbc.RowMapper;

import java.lang.reflect.Constructor;

public class EntityClass {
    private final Class<?> clazz;
    private final RowMapper<Object> rowMapper;
    private final EntityMetadata entityMetadata;

    private EntityClass(Class<?> clazz, EntityMetadata entityMetadata, RowMapper<Object> rowMapper) {
        this.clazz = clazz;
        this.entityMetadata = entityMetadata;
        this.rowMapper = rowMapper;
    }

    public static EntityClass of(Class<?> clazz) {
        Constructor<?> declaredConstructor;
        try {
            declaredConstructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        EntityMetadata entityMetadata = EntityMetadata.fromClass(clazz);
        return new EntityClass(
                clazz,
                entityMetadata,
                RowMapperFactory.create(declaredConstructor, entityMetadata, MySQLDialect.INSTANCE)
        );
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return clazz.getName();
    }

    public EntityMetadata getMetadata() {
        return entityMetadata;
    }

    public RowMapper<Object> getRowMapper() {
        return rowMapper;
    }

}
