package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;

public class InsertQueryBuilder<T> extends DMLQueryBuilder<T> {
    public InsertQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }

    public String insert(T queryValue) {
        return queryInsert(entityMeta.getTableName())
                + braceWithComma(
                    columnsClause(entityMeta.getEntityColumns())
                )
                + values(valueClause(queryValue));
    }


    private List<String> columnsClause(List<EntityColumn> entityColumns) {
        return entityMeta.getEntityColumns()
                .stream()
                .filter(column -> !column.hasGeneratedValue())
                .map(EntityColumn::getName)
                .collect(Collectors.toList());
    }

    private String valueClause(T object) {
        return entityMeta.getEntityColumns()
                .stream()
                .filter(column -> !column.hasGeneratedValue())
                .map(column -> columnValue(column, object))
                .collect(Collectors.joining(", "));
    }

    private String queryInsert(String tableName) {
        return dialect.insert(tableName);
    }

    private String values(String value) {
        return dialect.values(value);
    }


}
