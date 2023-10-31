package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;


public class UpdateQueryBuilder extends DMLQueryBuilder {

    private static final String EQUAL = "=";
    public UpdateQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }
    public String build(Object entity) {
        return updateQuery(entityMeta.getTableName())
                + updateValues(entityMeta.getEntityColumns(), entity)
                + whereId(getPkColumn(), getPkColumn().getFieldValue(entity));
    }

    private String updateQuery(String tableName)  {
        return dialect.updateForTableQuery(tableName);
    }

    private String updateValues(List<EntityColumn> entityColumns, Object entity) {
         return entityColumns
                .stream()
                .filter((it) -> !it.isPk())
                .map((it) -> it.getName() + EQUAL + getColumnValueString(it, entity))
                .collect(Collectors.joining(", "));
    }

}
