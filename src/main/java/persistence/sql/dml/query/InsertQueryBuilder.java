package persistence.sql.dml.query;

import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;

import java.util.List;

public class InsertQueryBuilder {
    private static final String EMPTY_STRING = "";
    private final StringBuilder query;

    public InsertQueryBuilder(Object entity) {
        query = new StringBuilder();
        final Class<?> entityClass = entity.getClass();
        final TableDefinition tableDefinition = new TableDefinition(entityClass);
        final List<? extends Queryable> targetColumns = tableDefinition.hasValueColumns(entity);

        query.append("INSERT INTO ");
        query.append(tableDefinition.tableName());

        query.append(" (");
        query.append(columnsClause(targetColumns));

        query.append(") VALUES (");
        query.append(valueClause(entity, targetColumns));
        query.append(");");
    }

    public String build() {
        return query.toString();
    }

    private String columnsClause(List<? extends Queryable> targetColumns) {
        return targetColumns
                .stream()
                .map(Queryable::getName)
                .reduce((column1, column2) -> column1 + ", " + column2)
                .orElse(EMPTY_STRING);
    }

    private String valueClause(Object object, List<? extends Queryable> targetColumns) {
        return targetColumns
                .stream()
                .map(column -> column.getValueAsString(object))
                .reduce((value1, value2) -> value1 + ", " + value2)
                .orElse(EMPTY_STRING);
    }

}
