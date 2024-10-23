package orm;

import orm.dsl.QueryBuilder;

public class DefaultEntityPersister implements EntityPersister {

    private final QueryBuilder queryBuilder;

    public DefaultEntityPersister(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @Override
    public <T> T persist(T entity) {
        return queryBuilder.insertInto(entity)
                .value(entity)
                .returnAsEntity();
    }

    @Override
    public <T> T update(T entity) {
        queryBuilder.update(entity).byId().execute();
        return entity;
    }

    @Override
    public void remove(Object entity) {
        queryBuilder.deleteFrom(entity).byId().execute();
    }
}
