package persistence.entity;

import jakarta.persistence.Id;
import jdbc.EntityMapper;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereClauseBuilder;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.EntityValue;
import persistence.sql.metadata.EntityValues;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SimpleEntityManager implements EntityManager{
    private final EntityPersister entityPersister;

    public SimpleEntityManager(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityValues entityValues = new EntityValues(Arrays.stream(clazz.getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Id.class))
                .map(x -> new EntityValue(x, String.valueOf(id)))
                .collect(Collectors.toList()));

        String query = new SelectQueryBuilder().buildFindByIdQuery(new EntityMetadata(clazz), new WhereClauseBuilder(entityValues));
        return entityPersister.getJdbcTemplate().queryForObject(query, new EntityMapper<>(clazz));
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
