package persistence.sql.dml;

import static persistence.sql.ddl.common.StringConstants.PRIMARY_KEY_NOT_FOUND;

import jakarta.persistence.Id;
import java.util.Arrays;
import persistence.sql.AbstractQueryBuilder;
import persistence.sql.Metadata;
import persistence.sql.ddl.TableQueryBuilder;

public class UpdateQueryBuilder extends AbstractQueryBuilder {
    private final TableQueryBuilder tableQueryBuilder;

    public UpdateQueryBuilder(TableQueryBuilder tableQueryBuilder) {
        this.tableQueryBuilder = tableQueryBuilder;
    }

    public String getUpdateQuery(Object entity) {
        Metadata metadata = new Metadata(entity.getClass());

        return String.format(
            "UPDATE %s SET %s WHERE %s = %s",
            tableQueryBuilder.getTableNameFrom(entity.getClass()),
            metadata.getEntityColumns().getUpdateQueryStringFrom(entity),
            getPrimaryKeyColumnName(entity.getClass()),
            getPrimaryKeyValueQueryFromEntity(entity)
        );
    }

    private Object getPrimaryKeyValueQueryFromEntity(Object entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Id.class))
            .findFirst()
            .map(field -> {
                field.setAccessible(true);
                try {
                    return field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            })
            .orElseThrow(() -> new IllegalStateException(PRIMARY_KEY_NOT_FOUND));
    }

    private String getPrimaryKeyColumnName(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Id.class))
            .findFirst()
            .map(this::getColumnNameFrom)
            .orElseThrow(() -> new IllegalStateException(PRIMARY_KEY_NOT_FOUND));
    }
}
