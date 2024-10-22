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
import java.util.stream.Collectors;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;
    private final TableDefinition tableDefinition;
    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Class<?> clazz,
                           PersistenceContext persistenceContext,
                           JdbcTemplate jdbcTemplate) {
        this.tableDefinition = new TableDefinition(clazz);
        this.persistenceContext = persistenceContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long getEntityId(Object entity) {
        if (alreadyHasId(entity)) {
            return (Long) tableDefinition.tableId().getValue(entity);
        }

        return DEFAULT_ID_VALUE;
    }

    public boolean alreadyHasId(Object entity) {
        return tableDefinition.tableId().hasValue(entity);
    }

    public void update(Object entity, List<String> targetColumns) {
        final Map<String, Object> modified = tableDefinition.withoutIdColumns().stream()
                .filter(column -> targetColumns.contains(column.getName()))
                .collect(Collectors.toMap(Queryable::getName, column -> column.hasValue(entity) ? column.getValueAsString(entity) : "null"));

        final String query = new UpdateQueryBuilder(entity).columns(modified).build();
        jdbcTemplate.execute(query);
    }

    public EntityKey insert(Object entity) {
        final Object id = getId(tableDefinition, entity);
        tableDefinition.tableId().bindValue(entity, id);

        final String query = new InsertQueryBuilder(entity).build();
        final Long insertedId = jdbcTemplate.insertAndReturnKey(query);
        return createEntityKey(entity, insertedId);
    }

    private EntityKey createEntityKey(Object entity, Long id) {
        final EntityKey entityKey = new EntityKey(id, entity.getClass());

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
    private Object getId(TableDefinition tableDefinition, Object entity) {
         if (tableDefinition.tableId().idRequired()) {
            if (alreadyHasId(entity)) {
                return getEntityId(entity);
            }

            throw new IllegalStateException("Identifier of entity "
                    + tableDefinition.tableName()
                    + " must be manually assigned before calling 'persist()'");
        }

        if (alreadyHasId(entity)) {
            throw new IllegalStateException(
                    "detached entity passed to persist: " + tableDefinition.tableName()
            );
        }
        return 0L;
    }

}
