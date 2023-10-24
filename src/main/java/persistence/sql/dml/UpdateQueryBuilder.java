package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;


public class UpdateQueryBuilder<T> extends DMLQueryBuilder<T> {

    private static final String EQUAL = "=";
    public UpdateQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }
    public String update(T entity) {
        return update(entityMeta.getTableName())
                + updateValues(entityMeta.getEntityColumns(), entity)
                + whereId(pkColumn(), pkColumn().getFieldValue(entity));
    }

    private String update(String tableName)  {
        return dialect.update(tableName);
    }

    private String updateValues(List<EntityColumn> entityColumns, T entity) {
         return entityColumns
                .stream()
                .filter((it) -> !it.isPk())
                .map((it) -> it.getName() + EQUAL +columnValue(it, entity))
                .collect(Collectors.joining(", "));
    }

}
