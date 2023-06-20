package persistence.entity;

import domain.Person;
import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.DmlBuilder;

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
    public <T> T find(Class<T> clazz, Long id) {
        EntityKey key = new EntityKey<>(clazz, id);
        if (!context.hasEntity(key)) {
            Object entity = jdbcTemplate.query(
                    dml.getFindByIdQuery(
                            Person.class, id
                    ),
                    new RowMapperImpl<>(clazz)
            ).get(0);
            context.persistEntity(entity);
            return (T) entity;
        }
        return context.findEntity(new EntityKey<>(clazz, id));
    }

    @Override
    public void persist(Object entity) {
        jdbcTemplate.execute(
                dml.getInsertQuery(entity)
        );
        context.removeEntity(entity);
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
}
