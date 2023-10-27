package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.EntityColumn;
import persistence.core.EntityColumns;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;

import java.util.List;
import java.util.Optional;

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

    public Optional<T> loadById(final Object id) {
        final String query = renderSelect(id);
        final List<T> result = jdbcTemplate.query(query, entityRowMapper::mapRow);
        if (result.size() > 1) {
            throw new PersistenceException("id 로 조회된 row 가 2개 이상입니다.");
        }

        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    public String renderSelect(final Object id) {
        return dmlGenerator.findById(tableName, columns.getNames(), idColumn.getName(), id);
    }
}
