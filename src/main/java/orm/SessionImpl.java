package orm;

import orm.dsl.QueryBuilder;
import orm.row_mapper.DefaultRowMapper;
import orm.settings.JpaSettings;

public class SessionImpl implements EntityManager {

    private final JpaSettings settings;

    private final StatefulPersistenceContext persistenceContext;

    public SessionImpl(QueryBuilder queryBuilder) {
        this.settings = JpaSettings.ofDefault();
        this.persistenceContext = new StatefulPersistenceContext(queryBuilder);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return persistenceContext.getEntity(clazz, id);
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
        return persistenceContext.addEntity(entity);
    }

    @Override
    public <T> T update(T entity) {
        persistenceContext.updateEntity(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
