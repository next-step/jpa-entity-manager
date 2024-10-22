package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;

    private final Logger logger = LoggerFactory.getLogger(EntityPersister.class);
    private final HashMap<Class<?>, TableDefinition> tableDefinitions;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.tableDefinitions = new HashMap<>();
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasId(Object entity) {
        putTableDefinitionIfAbsent(entity);

        final TableDefinition tableDefinition = tableDefinitions.get(entity.getClass());
        return tableDefinition.hasId(entity);
    }

    public Serializable getEntityId(Object entity) {
        putTableDefinitionIfAbsent(entity);

        final TableDefinition tableDefinition = tableDefinitions.get(entity.getClass());
        if (tableDefinition.hasId(entity)) {
            return tableDefinition.getIdValue(entity);
        }

        return DEFAULT_ID_VALUE;
    }

    public Object insert(Object entity) {
        putTableDefinitionIfAbsent(entity);

        final String query = new InsertQueryBuilder(entity).build();
        final Serializable id = jdbcTemplate.insertAndReturnKey(query);
        copyId(id, entity);
        return entity;
    }

    public void copyId(Serializable id, Object to) {
        try {
            final Class<?> entityClass = to.getClass();
            final TableDefinition tableDefinition = new TableDefinition(entityClass);
            final TableId tableId = tableDefinition.getTableId();

            final Field objectDeclaredField = entityClass.getDeclaredField(tableId.getDeclaredName());

            final boolean wasAccessible = objectDeclaredField.canAccess(to);
            if (!wasAccessible) {
                objectDeclaredField.setAccessible(true);
            }

            objectDeclaredField.set(to, id);

            if (!wasAccessible) {
                objectDeclaredField.setAccessible(false);
            }

        } catch (ReflectiveOperationException e) {
            logger.error("Failed to copy row to {}", to.getClass().getName(), e);
        }
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
}
