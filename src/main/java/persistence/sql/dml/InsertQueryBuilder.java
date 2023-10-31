package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;

public class InsertQueryBuilder extends DMLQueryBuilder {
    public InsertQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }

    public String build(Object queryValue) {
        validate(entityMeta, queryValue);

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

    private String valueClause(Object value) {
        return entityMeta.getEntityColumns()
                .stream()
                .filter(column -> !column.hasGeneratedValue())
                .map(column -> getColumnValueString(column, value))
                .collect(Collectors.joining(", "));
    }

    private String queryInsert(String tableName) {
        return dialect.insert(tableName);
    }

    private String values(String value) {
        return dialect.valuesQuery(value);
    }

    private void validate(EntityMeta entityMeta, Object queryValue) {
        if (!entityMeta.isAutoIncrement() && entityMeta.getPkValue(queryValue) == null) {
            throw new IllegalArgumentException("pk가 없습니다.");
        }
    }


}
