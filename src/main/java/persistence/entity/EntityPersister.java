package persistence.entity;

import jdbc.JdbcTemplate;
import org.jetbrains.annotations.Nullable;
import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;
    private final HashMap<Class<?>, TableDefinition> tableDefinitions;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.tableDefinitions = new HashMap<>();
        this.jdbcTemplate = jdbcTemplate;
    }

    public Serializable getEntityId(Object entity) {
        putTableDefinitionIfAbsent(entity);

        final TableDefinition tableDefinition = tableDefinitions.get(entity.getClass());
        if (tableDefinition.hasId(entity)) {
            return tableDefinition.getIdValue(entity);
        }

        return DEFAULT_ID_VALUE;
    }

    public EntityKey insert(Object entity) {
        putTableDefinitionIfAbsent(entity);

        final TableDefinition tableDefinition = tableDefinitions.get(entity.getClass());
        final Object id = getId(tableDefinition, entity);
        tableDefinition.getTableId().bindValue(entity, id);

        final String query = new InsertQueryBuilder(entity).build();
        final Long insertedId = jdbcTemplate.insertAndReturnKey(query);
        return createEntityKey(entity, insertedId);
    }

    private EntityKey createEntityKey(Object entity, Long id) {
        final EntityKey entityKey = new EntityKey(id, entity.getClass());

        entityKey.bindId(entity);
        return entityKey;
    }

    public void update(Object entity, List<String> targetColumns) {
        putTableDefinitionIfAbsent(entity);

        final TableDefinition tableDefinition = tableDefinitions.get(entity.getClass());
        final Map<String, Object> modified = tableDefinition.withoutIdColumns().stream()
                .filter(column -> targetColumns.contains(column.getName()))
                .collect(
                        Collectors.toMap(
                                Queryable::getName,
                                column -> column.hasValue(entity) ? column.getValueAsString(entity) : "null"
                        )
                );

        final String query = new UpdateQueryBuilder(entity).columns(modified).build();
        jdbcTemplate.execute(query);
    }

    private void putTableDefinitionIfAbsent(Object entity) {
        if (!tableDefinitions.containsKey(entity.getClass())) {
            tableDefinitions.put(entity.getClass(), new TableDefinition(entity.getClass()));
        }
    }

    public void delete(Object entity) {
        String query = new DeleteByIdQueryBuilder(entity).build();
        jdbcTemplate.execute(query);
    }

    @Nullable
    private Object getId(TableDefinition tableDefinition, Object entity) {
        if (tableDefinition.getTableId().idRequired()) {
            if (tableDefinition.hasId(entity)) {
                return getEntityId(entity);
            }

            throw new IllegalStateException("Identifier of entity "
                    + tableDefinition.getTableName()
                    + " must be manually assigned before calling 'persist()'");
        }

        if (tableDefinition.hasId(entity)) {
            throw new IllegalStateException(
                    "detached entity passed to persist: " + tableDefinition.getTableName()
            );
        }
        return 0L;
    }

}
