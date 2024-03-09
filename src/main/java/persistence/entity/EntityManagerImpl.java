package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.common.DtoMapper;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.List;
import java.util.Optional;

public class EntityManagerImpl implements EntityManager{

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        String query = new SelectQueryBuilder(clazz).getFindById(id);
        List<T> selected = jdbcTemplate.query(query, new DtoMapper<>(clazz));
        return getFoundResult(selected);
    }

    private <T> Optional<T> getFoundResult(List<T> selected) {
        if (selected.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(selected.get(0));
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
