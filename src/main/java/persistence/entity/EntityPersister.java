package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.ddl.EntityMetadata;
import persistence.sql.dml.EntityManipulationBuilder;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T insert(T entity) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass());

        long key = jdbcTemplate.executeAndReturnKey(
                new EntityManipulationBuilder().insert(entity, entityMetadata)
        );
        entityMetadata.setIdToEntity(entity, key);

        return entity;
    }

    public <T> void remove(T entity, String id) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass());
        jdbcTemplate.execute(new EntityManipulationBuilder().delete(id, entityMetadata));
    }

    public <T> void update(T entity, T snapshot) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass());
        String update = new EntityManipulationBuilder().update(entity, snapshot, entityMetadata);
        if (!update.isEmpty()) {
            jdbcTemplate.execute(update);
        }
    }

}
