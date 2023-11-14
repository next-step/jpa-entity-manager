package persistence.entity;

import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.usecase.GetFieldValueUseCase;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;
import persistence.sql.usecase.SetFieldValueUseCase;
import persistence.sql.vo.DatabaseField;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase;
    private final GetFieldValueUseCase getFieldValueUseCase;
    private final SetFieldValueUseCase setFieldValueUseCase;


    public EntityManagerImpl(EntityLoader entityLoader, EntityPersister entityPersister,
                             GetIdDatabaseFieldUseCase getIdDatabaseFieldUseCase,
                             GetFieldValueUseCase getFieldValueUseCase,
                             SetFieldValueUseCase setFieldValueUseCase) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
        this.getIdDatabaseFieldUseCase = getIdDatabaseFieldUseCase;
        this.getFieldValueUseCase = getFieldValueUseCase;
        this.setFieldValueUseCase = setFieldValueUseCase;
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        return entityLoader.find(clazz, Id);
    }

    @Override
    public Object persist(Object entity) {
        Object idValue = getFieldValueUseCase.execute(entity, getIdDatabaseFieldUseCase.execute(entity.getClass()));
        if (idValue != null) {
            entityPersister.update(entity);
            return entity;
        }
        Long id = entityPersister.insert(entity);
        DatabaseField databaseField = getIdDatabaseFieldUseCase.execute(entity.getClass());
        setFieldValueUseCase.execute(entity, databaseField, id);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
