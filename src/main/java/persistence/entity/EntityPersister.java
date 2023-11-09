package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.ddl.EntityMetadata;
import persistence.sql.ddl.dialect.Dialect;
import persistence.sql.dml.EntityManipulationBuilder;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    private final Dialect dialect;

    public EntityPersister(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> T findById(Class<T> clazz, Long id) {
        EntityManipulationBuilder manipulationBuilder = createManipulationBuilder(clazz);
        return jdbcTemplate.queryForObject(manipulationBuilder.findById(id), resultSet -> {
            if (!resultSet.next()) {
                return null;
            }

            return manipulationBuilder.getEntity(resultSet);
        });
    }

    public <T> T insert(T entity) {
        EntityManipulationBuilder manipulationBuilder = createManipulationBuilder(entity.getClass());
        long key = jdbcTemplate.executeAndReturnKey(manipulationBuilder.insert(entity));
        manipulationBuilder.setIdToEntity(entity, key);

        return entity;
    }

    public <T> void remove(T entity, String id) {
        jdbcTemplate.execute(createManipulationBuilder(entity.getClass()).delete(id));
    }

    private <T>EntityManipulationBuilder createManipulationBuilder(Class<T> clazz) {
        return new EntityManipulationBuilder(EntityMetadata.of(clazz, dialect));
    }

}
