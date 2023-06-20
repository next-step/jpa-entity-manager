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
                context.findEntity(new EntityKey<>(clazz, id))
        );
    }

    @Override
    public void persist(Object entity) {
        final String query = hasEntity(entity)
                ? dml.getUpdateQuery(entity)
                : dml.getInsertQuery(entity);
        jdbcTemplate.execute(query);
        context.persistEntity(entity);
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

    public <T> Optional<T> findFromDB(EntityKey<T> key) {
        Class<T> clazz = key.getEntityClass();
        List<T> entities = jdbcTemplate.query(
                dml.getFindByIdQuery(clazz, key.getEntityId()),
                new RowMapperImpl<>(clazz)
        );
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        Object entity = entities.get(0);
        context.persistEntity(entity);
        return Optional.of((T) entity);
    }

    private boolean hasEntity(Object entity) {
        return find(
                entity.getClass(),
                new EntityKey(entity).getEntityId()
        ).isPresent();
    }
}
