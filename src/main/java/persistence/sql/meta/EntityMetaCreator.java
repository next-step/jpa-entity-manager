package persistence.sql.meta;

public interface EntityMetaCreator {

    Table createByClass(Class<?> clazz);
    Table createByInstance(Object object);
}
