package persistence.entity;

import jdbc.JdbcTemplate;
import org.jetbrains.annotations.Nullable;
import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityPersister {
    private static final String DEFAULT_ID_VALUE = "0";
    private final TableDefinition tableDefinition;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Class<?> clazz,
                           JdbcTemplate jdbcTemplate) {
        this.tableDefinition = new TableDefinition(clazz);
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object getEntityId(Object entity) {
        return tableDefinition.tableId().getValue(entity);
    }

    public boolean alreadyHasId(Object entity) {
        return tableDefinition.tableId().hasValue(entity);
    }

    public boolean isNew(Object entity) {
        final boolean hasNullId = tableDefinition.tableId().hasValue(entity);
        return hasNullId || isIdDefaultValue(tableDefinition.tableId().getValue(entity));
    }

    public void update(Object entity, List<String> targetColumns) {
        final Map<String, Object> modified = tableDefinition.withoutIdColumns().stream()
                .filter(column -> targetColumns.contains(column.getName()))
                .collect(Collectors.toMap(Queryable::getName, column -> column.hasValue(entity) ? column.getValue(entity) : "null"));

        final String query = new UpdateQueryBuilder(entity).columns(modified).build();
        jdbcTemplate.execute(query);
    }

    public EntityKey insert(Object entity) {
        final Object id = decideId(tableDefinition, entity);
        tableDefinition.tableId().bindValue(entity, id);

        final String query = new InsertQueryBuilder(entity).build();
        final Long insertedId = jdbcTemplate.insertAndReturnKey(query);
        final EntityKey entityKey = new EntityKey(insertedId, entity.getClass());

        entityKey.bindId(entity);
        return entityKey;
    }

    public void delete(Object entity) {
        String query = new DeleteByIdQueryBuilder(entity).build();
        jdbcTemplate.execute(query);
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }

    @Nullable
    private Object decideId(TableDefinition tableDefinition, Object entity) {
        if (tableDefinition.tableId().shouldFetchId()) {
            if (alreadyHasId(entity)) {
                return getEntityId(entity);
            }

            return jdbcTemplate.getLastId(tableDefinition.tableName()) + 1;
        }

        return 0;
    }

    private boolean isIdDefaultValue(Object id) {
        if (id instanceof Long) {
            return (Long) id == 0L;
        }

        if (id instanceof Integer) {
            return (Integer) id == 0;
        }

        if (id instanceof String) {
            return id.equals(DEFAULT_ID_VALUE);
        }

        return false;
    }
}
