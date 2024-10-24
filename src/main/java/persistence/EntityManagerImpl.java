package persistence;

import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityLoader = new EntityLoader(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return this.entityLoader.find(clazz, id);
    }

    @Override
    public void persist(Object entityInstance) {
        this.entityLoader.persist(entityInstance);
    }

    @Override
    public void merge(Object entityInstance) {
        this.entityLoader.merge(entityInstance);
    }

    @Override
    public void remove(Object entityInstance) {
        this.entityLoader.remove(entityInstance);
    }
}
