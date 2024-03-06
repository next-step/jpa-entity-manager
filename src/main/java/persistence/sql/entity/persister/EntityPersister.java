package persistence.sql.entity.persister;

public interface EntityPersister {

    boolean update(Object entity);

    void insert(Object entity);

    Object insertWithPk(Object entity);

    void delete(Object entity);

    void deleteAll(Class<?> clazz);

}
