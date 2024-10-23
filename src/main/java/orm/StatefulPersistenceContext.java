package orm;

import orm.dsl.QueryBuilder;
import orm.settings.JpaSettings;

import java.util.HashMap;
import java.util.Map;

public class StatefulPersistenceContext implements PersistenceContext {

    private Map<EntityKey, Object> cachedEntities;
    private final EntityPersister entityPersister;
    private final QueryBuilder queryBuilder;
    private final JpaSettings settings;

    public StatefulPersistenceContext(QueryBuilder queryBuilder) {
        this.cachedEntities = new HashMap<>();
        this.queryBuilder = queryBuilder;
        this.settings = queryBuilder.getSettings();
        this.entityPersister = new DefaultEntityPersister(queryBuilder);
    }

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey(clazz, id);
        var cachedEntity = cachedEntities.get(entityKey);
        if (cachedEntity != null) {
            return castEntity(clazz, cachedEntity);
        }

        T entity = queryBuilder.selectFrom(clazz)
                .findById(entityKey.getIdValue())
                .fetchOne();

        if (entity != null) {
            this.addEntity(entity);
        }

        return entity;
    }

    private <T> T castEntity(Class<T> clazz, Object persistedEntity) {
        if (!clazz.isInstance(persistedEntity)) {
            throw new IllegalArgumentException("Invalid type for persisted entity");
        }

        return clazz.cast(persistedEntity);
    }

    @Override
    public <T> T addEntity(T entity) {
        var tableEntity = new TableEntity<>(entity, settings);
        var entityKey = new EntityKey(tableEntity);

        T persistedEntity = entityPersister.persist(entity);
        cachedEntities.put(entityKey, persistedEntity);
        return persistedEntity;
    }

    @Override
    public boolean containsEntity(EntityKey key) {
        return cachedEntities.containsKey(key);
    }

    @Override
    public void updateEntity(Object entity) {
        entityPersister.update(entity);
    }

    @Override
    public void removeEntity(Object entity) {
        var tableEntity = new TableEntity<>(entity, settings);
        var entityKey = new EntityKey(tableEntity);
        cachedEntities.remove(entityKey);
        entityPersister.remove(entity);
    }

    @Override
    public void clear() {
        cachedEntities = new HashMap<>();
    }
}
