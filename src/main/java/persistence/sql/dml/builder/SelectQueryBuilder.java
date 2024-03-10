package persistence.sql.dml.builder;

import persistence.sql.dml.model.DMLColumn;
import persistence.sql.model.Table;

public class SelectQueryBuilder {

    private static final String FIND_ALL_QUERY_FORMAT = "SELECT %s FROM %s;";
    private static final String FIND_BY_ID_QUERY_FORMAT = "SELECT %s FROM %s WHERE %s = %s;";

    private final Table table;
    private final DMLColumn column;

    public SelectQueryBuilder(Table table, DMLColumn column) {
        this.table = table;
        this.column = column;
    }

    public Builder findAll() {
        return new Builder(
                String.format(
                        FIND_ALL_QUERY_FORMAT,
                        column.getAllColumnClause(),
                        table.name()
                )
        );
    }

    public Builder findById(Object id) {
        return new Builder(
                String.format(
                        FIND_BY_ID_QUERY_FORMAT,
                        column.getAllColumnClause(),
                        table.name(),
                        column.getIdColumnName(),
                        id
                )
        );
    }

    public static class Builder {
        private final String query;

        public Builder(String query) {
            this.query = query;
        }

        public String build() {
            return query;
        }
    }
}
