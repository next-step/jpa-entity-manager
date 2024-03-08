package persistence.sql.dml;

import persistence.entity.EntityId;
import persistence.sql.model.Table;

import java.util.List;

public class FindQueryBuilder {

    private final static String FIND_ALL_QUERY_FORMAT = "SELECT %s FROM %s;";
    private final static String FIND_BY_ID_QUERY_FORMAT = "SELECT %s FROM %s WHERE %s;";

    private final Table table;

    public FindQueryBuilder(Table table) {
        this.table = table;
    }

    public String build() {
        String columnsClause = buildColumnsClause();
        String tableName = table.getName();
        return String.format(FIND_ALL_QUERY_FORMAT, columnsClause, tableName);
    }

    public String buildById(EntityId id) {
        ByIdQueryBuilder byIdQueryBuilder = new ByIdQueryBuilder(table, id);

        String columnsClause = buildColumnsClause();
        String tableName = table.getName();
        String whereClause = byIdQueryBuilder.build();

        return String.format(FIND_BY_ID_QUERY_FORMAT, columnsClause, tableName, whereClause);
    }

    private String buildColumnsClause() {
        StringBuilder columnsClauseBuilder = new StringBuilder();

        List<String> columnNames = table.getAllColumnNames();
        String joinedColumnNames = String.join(",", columnNames);
        columnsClauseBuilder.append(joinedColumnNames);

        return columnsClauseBuilder.toString();
    }
}
