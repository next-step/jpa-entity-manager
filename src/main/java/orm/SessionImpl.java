package orm;

import orm.dsl.QueryBuilder;
import orm.row_mapper.DefaultRowMapper;
import orm.settings.JpaSettings;

public class SessionImpl implements EntityManager {

    private final QueryBuilder queryBuilder;
    private final JpaSettings settings;

    private final StatefulPersistenceContext persistenceContext;

    public SessionImpl(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
        this.settings = JpaSettings.ofDefault();
        this.persistenceContext = new StatefulPersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey(clazz, id);
        Object persistedEntity = persistenceContext.getEntity(entityKey);
        if (persistedEntity != null) {
            return castEntity(clazz, persistedEntity);
        }

        T entity = queryBuilder.selectFrom(clazz).findById(id)
                .fetchOne(new DefaultRowMapper<>(clazz));

        persistenceContext.addEntity(entityKey, entity);
        return entity;
    }

    private <T> T castEntity(Class<T> clazz, Object persistedEntity) {
        if (!clazz.isInstance(persistedEntity)) {
            throw new IllegalArgumentException("Invalid type for persisted entity");
        }

        return clazz.cast(persistedEntity);
    }

    /**
     * 엔티티 저장
     * <p>
     * 엔티티메니저에서는 bulk insert가 불가능 하지만
     * QueryBuilder 를 직접 쓰면 가능함.
     *
     * @param entity 엔티티 클래스
     * @return 엔티티
     */
    @Override
    public <T> T persist(T entity) {
        var tableEntity = new TableEntity<>(entity, settings);
        final T persistedEntity = queryBuilder.insertInto(tableEntity)
                .value(tableEntity.getEntity())
                .returnAsEntity();

        persistenceContext.addEntity(new EntityKey(tableEntity), tableEntity.getEntity());
        return persistedEntity;
    }

    @Override
    public <T> T update(T entity) {
        queryBuilder.update(entity).byId().execute();
        return entity;
    }

    @Override
    public void remove(Object entity) {
        var tableEntity = new TableEntity<>(entity, settings);
        persistenceContext.removeEntity(new EntityKey(tableEntity));
        queryBuilder.deleteFrom(tableEntity).byId().execute();
    }
}
