package persistence.sql.dml.update;

import java.util.LinkedHashMap;
import java.util.Map;
import persistence.sql.dml.ColumnClause;
import persistence.sql.dml.ValueClause;
import persistence.sql.dml.where.WhereQuery;
import persistence.sql.vo.TableName;

public class UpdateQuery {
    private final TableName tableName;
    private final WhereQuery whereQuery;
    private final Map<ColumnClause, ValueClause> columToValueMap = new LinkedHashMap<>();

    public UpdateQuery(TableName tableName, WhereQuery whereQuery) {
        this.tableName = tableName;
        this.whereQuery = whereQuery;
    }

    public TableName getTableName() {
        return tableName;
    }

    public void addFieldValue(ColumnClause columnClause, ValueClause valueClause) {
        this.columToValueMap.put(columnClause, valueClause);
    }

    public Map<ColumnClause, ValueClause> getColumToValueMap() {
        return columToValueMap;
    }

    public WhereQuery getWhereQuery() {
        return whereQuery;
    }
}
