package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.DmlBuilder;

import java.util.List;
import java.util.Optional;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext context;
    private final JdbcTemplate jdbcTemplate;
    private final DmlBuilder dml;

    public EntityManagerImpl(PersistenceContext context, JdbcTemplate jdbcTemplate, DmlBuilder dml) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.dml = dml;
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Object id) {
        EntityKey<T> key = new EntityKey<>(clazz, id);
        if (!context.hasEntity(key)) {
            return findFromDB(key);
        }
        return Optional.of(
                context.getEntity(new EntityKey<>(clazz, id))
        );
    }

    @Override
    public void persist(Object entity) {
        final String query = hasEntity(entity)
                ? dml.getUpdateQuery(entity)
                : dml.getInsertQuery(entity);
        jdbcTemplate.execute(query);
        context.addEntity(entity);
        context.getDatabaseSnapshot(
                new EntityKey<>(entity),
                EntityHelper.clone(entity)
        );
    }

    @Override
    public void remove(Object entity) {
        EntityKey key = new EntityKey(entity);
        if (!context.hasEntity(key)) {
            return;
        }
        jdbcTemplate.execute(dml.getDeleteByIdQuery(
                entity.getClass(),
                key.getEntityId()
        ));
        context.removeEntity(entity);
    }

    @Override
    public void merge(Object entity) {
        persist(entity);
        context.getDatabaseSnapshot(
                new EntityKey<>(entity), entity
        );
    }

    @Override
    public void detach(Object entity) {
        context.removeEntity(entity);
    }

    @Override
    public boolean isDirty(Object entity) {
        return !hasEntity(entity) || !EntityHelper.equals(
                entity,
                context.getCachedDatabaseSnapshot(new EntityKey<>(entity))
        );
    }

    private <T> Optional<T> findFromDB(EntityKey<T> key) {
        Class<T> clazz = key.getEntityClass();
        List<T> entities = jdbcTemplate.query(
                dml.getFindByIdQuery(clazz, key.getEntityId()),
                new RowMapperImpl<>(clazz)
        );
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        T entity = entities.get(0);
        context.addEntity(entity);
        return Optional.of(context.getDatabaseSnapshot(
                new EntityKey<>(entity), entity
        ));
    }

    private boolean hasEntity(Object entity) {
        return find(
                entity.getClass(),
                new EntityKey(entity).getEntityId()
        ).isPresent();
    }
}
