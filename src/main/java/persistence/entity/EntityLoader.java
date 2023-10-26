package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.EntityColumn;
import persistence.core.EntityColumns;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.sql.dml.DmlGenerator;

public class EntityLoader<T> {
    private final String tableName;
    private final EntityColumn idColumn;
    private final EntityColumns columns;
    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;
    private final EntityRowMapper<T> entityRowMapper;

    public EntityLoader(final Class<T> clazz, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        final EntityMetadata<T> entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(clazz);
        this.tableName = entityMetadata.getTableName();
        this.idColumn = entityMetadata.getIdColumn();
        this.columns = entityMetadata.getColumns();
        this.dmlGenerator = dmlGenerator;
        this.jdbcTemplate = jdbcTemplate;
        this.entityRowMapper = new EntityRowMapper<>(clazz);
    }

    public T loadById(final Object id) {
        final String query = renderSelect(id);
        return jdbcTemplate.queryForObject(query, entityRowMapper::mapRow);
    }

    public String renderSelect(final Object id) {
        return dmlGenerator.findById(tableName, columns.getNames(), idColumn.getName(), id);
    }
}
