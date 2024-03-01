package persistence.sql.dml;

import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import java.util.ArrayList;

public class SelectQueryBuilder {
    private final TableData table;
    private final Columns columns;

    public SelectQueryBuilder(TableData table, Columns columns) {
        this.table = table;
        this.columns = columns;
    }

    public String build(WhereBuilder whereBuilder) {
        StringBuilder query = new StringBuilder();
        query.append("select ");
        query.append(selectClause());
        query.append(" from ");
        query.append(table.getName());

        if(whereBuilder.isEmpty()) {
            return query.toString();
        }

        query.append(whereBuilder.toClause());

        return query.toString();
    }

    private String selectClause() {
        ArrayList<String> names = new ArrayList<String>();
        names.add(columns.getKeyColumnName());
        names.addAll(columns.getNames());
        return String.join(", ", names);
    }
}
