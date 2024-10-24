package persistence;

import builder.dml.DMLBuilderData;
import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.persistenceContext = new PersistenceContextImpl();
    }

    public EntityManagerImpl(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityKey<T> entityObject = new EntityKey<>(id, clazz);
        Object persistObject = this.persistenceContext.findEntity(entityObject);
        if (persistObject != null) {
            return clazz.cast(persistObject);
        }
        T findObject = this.entityLoader.find(clazz, id);
        this.persistenceContext.insertEntity(new EntityKey<>(id, findObject.getClass()), findObject);
        return findObject;
    }

    @Override
    public void persist(Object entityInstance) {
        this.entityPersister.persist(entityInstance);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.persistenceContext.insertEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    @Override
    public void merge(Object entityInstance) {
        this.entityPersister.merge(entityInstance);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.persistenceContext.insertEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    @Override
    public void remove(Object entityInstance) {
        this.entityPersister.remove(entityInstance);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.persistenceContext.deleteEntity(new EntityKey<>(dmlBuilderData.getId(), entityInstance.getClass()));
    }
}
