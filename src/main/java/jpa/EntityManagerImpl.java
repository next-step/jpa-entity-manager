package jpa;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQuery;
import persistence.sql.dml.InsertQuery;
import persistence.sql.dml.SelectQuery;
import persistence.sql.dml.UpdateQuery;
import persistence.sql.entity.EntityRowMapper;

import java.util.Objects;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;

    public EntityManagerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityPersister.find(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public void update(Object entity) {
        entityPersister.update(entity);
    }

}
