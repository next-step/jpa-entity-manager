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

    public <T> T insert(T entity) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass(), dialect);
        long key = jdbcTemplate.executeAndReturnKey(EntityManipulationBuilder.insert(entity, entityMetadata));
        entityMetadata.setIdToEntity(entity, key);

        return entity;
    }

    public <T> void remove(T entity, String id) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass(), dialect);
        jdbcTemplate.execute(EntityManipulationBuilder.delete(id, entityMetadata));
    }

}
