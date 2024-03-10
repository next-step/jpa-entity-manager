package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.entity.exception.NotUniqueDataException;
import persistence.sql.common.DtoMapper;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;
import java.util.Optional;

public class EntityManagerImpl implements EntityManager{
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.entityLoader = new EntityLoader(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        return entityLoader.find(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
