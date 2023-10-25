package persistence.entity;


import java.util.List;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.exception.NotFoundException;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class DefaultEntityManager implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta entityMeta;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final Dialect dialect;

    public DefaultEntityManager(JdbcTemplate jdbcTemplate, EntityMeta entityMeta, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.entityLoader = new EntityLoader(entityMeta);
        this.dialect = dialect;
        this.entityPersister = new EntityPersister(jdbcTemplate, entityMeta, dialect);
    }


    @Override
    public <T> T persist(T entity) {
        entityPersister.insert(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        if (id == null) {
            throw new NotFoundException("id가 널이면 안 됩니다.");
        }

        final String query = QueryGenerator.of(entityMeta, dialect)
                .select()
                .findByIdQuery(id);

        return jdbcTemplate.queryForObject(query, (resultSet) -> entityLoader.resultSetToEntity(clazz, resultSet));
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final String query = QueryGenerator.of(entityMeta, dialect)
                .select()
                .findAllQuery();

        return jdbcTemplate.query(query, (resultSet) -> entityLoader.resultSetToEntity(tClass, resultSet));
    }


}
