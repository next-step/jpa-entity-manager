package persistence.sql.dml;

import persistence.entity.EntityBinder;
import persistence.entity.EntityId;
import persistence.sql.model.Column;
import persistence.sql.model.Columns;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private final static String UPDATE_BY_ID_QUERY_BUILDER = "UPDATE %s SET (%s) = (%s) WHERE %s;";

    private final Table table;
    private final EntityBinder entityBinder;

    public UpdateQueryBuilder(Table table, Object entity) {
        this.table = table;
        this.entityBinder = new EntityBinder(entity);
    }

    public String buildById(EntityId id) {
        ByIdQueryBuilder byIdQueryBuilder = new ByIdQueryBuilder(table, id);

        String tableName = table.getName();
        String columnsClause = buildColumnsClause();
        String columnsValueClause = buildColumnsValueClause();
        String whereClause = byIdQueryBuilder.build();
        return String.format(UPDATE_BY_ID_QUERY_BUILDER, tableName, columnsClause, columnsValueClause, whereClause);
    }

    private String buildColumnsClause() {
        List<String> allColumnNames = table.getAllColumnNames();
        return String.join(",", allColumnNames);
    }

    private String buildColumnsValueClause() {
        PKColumn pkColumn = table.getPKColumn();
        Object idValue = entityBinder.getValue(pkColumn);
        EntityId id = new EntityId(idValue);
        String pkValueClause = String.format("%s,", id);

        Columns columns = table.getColumns();
        return columns.stream()
                .map(this::buildColumnValueClause)
                .collect(Collectors.joining(",", pkValueClause, ""));
    }

    private String buildColumnValueClause(Column column) {
        StringBuilder valueClauseBuilder = new StringBuilder();

        Object value = entityBinder.getValue(column);
        if (column.isType(String.class)) {
            return valueClauseBuilder.append('\'')
                    .append(value)
                    .append('\'')
                    .toString();
        }

        valueClauseBuilder.append(value);
        return valueClauseBuilder.toString();
    }
}
