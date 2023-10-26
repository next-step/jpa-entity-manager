package persistence.sql.dml;

import persistence.dialect.Dialect;
import persistence.exception.FieldEmptyException;
import persistence.meta.ColumnType;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;
import persistence.sql.QueryBuilder;

public class DMLQueryBuilder extends QueryBuilder {
    protected DMLQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }

    protected String getFromTableQuery(String tableName) {
        return dialect.getFromTableQuery(tableName);
    }

    protected String whereId(EntityColumn column, Object id) {
        if (column.isVarchar()) {
            return dialect.whereId(column.getName(), "'" + id + "'");
        }
        return dialect.whereId(column.getName(), id.toString());
    }

    protected EntityColumn getPkColumn() {
        return entityMeta.getEntityColumns()
                .stream()
                .filter(EntityColumn::isPk)
                .findFirst()
                .orElseThrow(() -> new FieldEmptyException("pk가 없습니다."));
    }

    protected String getColumnValueString(EntityColumn column, Object entity) {
        final ColumnType columType = column.getColumnType();
        final Object value = column.getFieldValue(entity);
        if (value == null) {
            return "null";
        }
        if (columType.isVarchar()) {
            return "'" + value + "'";
        }
        return value.toString();
    }

}
