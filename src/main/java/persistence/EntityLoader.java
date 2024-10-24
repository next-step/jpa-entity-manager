package persistence;

import builder.dml.DMLBuilderData;
import jdbc.JdbcTemplate;

public class EntityLoader {

    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl();
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    public EntityLoader(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    public <T> T find(Class<T> clazz, Long id) {
        EntityInfo<T> entityObject = new EntityInfo<>(id, clazz);
        Object persistObject = this.persistenceContext.findEntity(entityObject);
        if (persistObject != null) {
            return clazz.cast(persistObject);
        }
        T findObject = this.entityPersister.find(clazz, id);
        this.persistenceContext.insertEntity(new EntityInfo<>(id, findObject.getClass()), findObject);
        return findObject;
    }

    public void persist(Object entityInstance) {
        this.entityPersister.persist(entityInstance);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.persistenceContext.insertEntity(new EntityInfo<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    public void merge(Object entityInstance) {
        this.entityPersister.merge(entityInstance);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.persistenceContext.insertEntity(new EntityInfo<>(dmlBuilderData.getId(), entityInstance.getClass()), entityInstance);
    }

    public void remove(Object entityInstance) {
        this.entityPersister.remove(entityInstance);
        DMLBuilderData dmlBuilderData = DMLBuilderData.createDMLBuilderData(entityInstance);
        this.persistenceContext.deleteEntity(new EntityInfo<>(dmlBuilderData.getId(), entityInstance.getClass()));
    }
}
