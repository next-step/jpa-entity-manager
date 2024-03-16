package persistence.sql;

import static persistence.sql.ddl.common.StringConstants.COLUMN_DEFINITION_DELIMITER;

import jakarta.persistence.Column;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

public abstract class AbstractQueryBuilder {

    protected AbstractQueryBuilder() {

    }

    /**
     * Get the column names query from the entity metadata
     * @param entityMetadata Entity metadata
     * @return Column names query
     */
    protected String getColumnNamesQuery(EntityMetadata entityMetadata) {
        return entityMetadata.getEntityColumns().stream()
            .map(EntityColumn::getColumnName)
            .collect(Collectors.joining(COLUMN_DEFINITION_DELIMITER));
    }


    /**
     * Get the primary key column name query from the entity metadata
     * @param entityMetadata Entity metadata
     * @return Primary key column name query
     */
    protected String getPrimaryKeyColumnNameQuery(EntityMetadata entityMetadata) {
        return entityMetadata
            .getEntityIdColumn()
            .getColumnName();
    }

    /**
     * Get the column names query from the entity metadata
     * @param entityMetadata Entity metadata
     * @param entityClass Entity class
     * @param id Entity id
     * @return Id query
     */
    protected String getIdQuery(EntityMetadata entityMetadata, Class<?> entityClass, Object id) {
        return entityMetadata
            .getEntityIdFrom(entityClass, id)
            .queryString();
    }

    /**
     * Get the id query from the entity metadata and entity
     * @param entityMetadata Entity metadata
     * @param entity Entity object
     * @return Id query
     */
    protected String getIdQuery(EntityMetadata entityMetadata, Object entity) {
        return entityMetadata
            .getEntityIdFrom(entity)
            .queryString();
    }

    /**
     * Get the column name from the field
     * @param field Field
     * @return Column name from the field
     */
    protected String getColumnNameFrom(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            return field.getName();
        }

        Column column = field.getAnnotation(Column.class);

        if (column.name().isEmpty()) {
            return field.getName();
        }

        return column.name();
    }
}
