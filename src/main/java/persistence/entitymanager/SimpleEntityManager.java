package persistence.entitymanager;

import jdbc.JdbcTemplate;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister persister;
    private final EntityLoader loader;

    public SimpleEntityManager(JdbcTemplate template) {
        this.persister = new EntityPersister(template);
        this.loader = new EntityLoader(template);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = loader.load(clazz, id);
        return entity;
    }

    @Override
    public Object persist(Object entity) {
        persister.insert(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        persister.delete(entity);
    }
}
