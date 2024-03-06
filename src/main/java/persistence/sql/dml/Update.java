package persistence.sql.dml;

import persistence.sql.mapping.Columns;
import persistence.sql.mapping.Table;

import java.util.List;

public class Update {

    private final Table table;

    private final Columns columns;

    private final Wheres whereClause;

    public Update(final Table table) {
        this.table = table;
        this.columns = new Columns(table.getColumns());
        final List<Where> wheres = this.columns.generatePkColumnWheres();
        this.whereClause = new Wheres(wheres);
    }

    public Table getTable() {
        return table;
    }

    public String getWhereClause() {
        return this.whereClause.wheresClause();
    }

    public String columnSetClause() {
        return columns.columnSetClause();
    }

}
