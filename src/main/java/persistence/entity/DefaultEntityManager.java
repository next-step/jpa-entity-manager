package persistence.entity;


import java.util.List;
import jdbc.JdbcTemplate;
import persistence.exception.NotFoundException;

import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class DefaultEntityManager implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta entityMeta;
    private final EntityMapper entityMapper;

    public DefaultEntityManager(JdbcTemplate jdbcTemplate, EntityMeta entityMeta) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.entityMapper = new EntityMapper(entityMeta);
    }


    @Override
    public <T> T persist(T entity) {
        final EntityPersister<T> entityPersister = (EntityPersister<T>) new EntityPersister<>(jdbcTemplate, entity.getClass());

        entityPersister.insert(entity);

        return entity;
    }


    @Override
    public void remove(Object entity) {
        final String query = QueryGenerator.from(entityMeta).delete(entityMeta.getPkValue(entity));

        jdbcTemplate.execute(query);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        if (id == null) {
            throw new NotFoundException("id가 널이면 안 됩니다.");
        }

        final String query = QueryGenerator.from(entityMeta)
                .select()
                .findById(id);

        return jdbcTemplate.queryForObject(query, (resultSet) -> entityMapper.resultSetToEntity(clazz, resultSet));
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final String query = QueryGenerator.from(entityMeta)
                .select()
                .findAll();

        return jdbcTemplate.query(query, (resultSet) -> entityMapper.resultSetToEntity(tClass, resultSet));
    }


}
