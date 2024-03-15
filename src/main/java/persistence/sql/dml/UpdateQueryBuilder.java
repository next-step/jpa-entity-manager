package persistence.sql.dml;

import java.util.stream.Collectors;
import persistence.sql.AbstractQueryBuilder;
import persistence.sql.EntityMetadata;
import persistence.sql.ddl.common.StringConstants;

public class UpdateQueryBuilder extends AbstractQueryBuilder {

    public String getUpdateQuery(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());

        return String.format(
            "UPDATE %s SET %s WHERE %s = %s",
            entityMetadata.getTableName(),
            getUpdateColumnsQuery(entityMetadata, entity),
            getPrimaryKeyColumnNameQuery(entityMetadata),
            getIdQuery(entityMetadata, entity)
        );
    }

    private String getUpdateColumnsQuery(EntityMetadata entityMetadata, Object entity) {
        return entityMetadata.getEntityColumns()
            .stream()
            .filter(column -> !column.isPrimary())
            .map(column ->
                String.format(
                    "%s = %s",
                    column.getColumnName(),
                    column.getEntityColumnValueFrom(entity).queryString()
                )
            ).collect(Collectors.joining(StringConstants.COLUMN_DEFINITION_DELIMITER));
    }
}
