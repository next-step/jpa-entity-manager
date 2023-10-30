package persistence.sql.dml;

import persistence.sql.dbms.Dialect;
import persistence.sql.dml.clause.WhereClause;
import persistence.sql.dml.clause.operator.WhereClauseSQLBuilder;
import persistence.sql.entitymetadata.model.EntityColumn;

import java.util.stream.Collectors;

public class UpdateDMLQueryBuilder<E> extends DMLQueryBuilder<E> {

    private E entity;
    private WhereClause whereClause;

    public UpdateDMLQueryBuilder(Dialect dialect, E entity) {
        super(dialect, (Class<E>) entity.getClass());
        this.entity = entity;
    }

    public UpdateDMLQueryBuilder<E> where(WhereClause whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    @Override
    public String build() {
        return String.format("UPDATE %s \n" +
                        "SET %s \n" +
                        "%s;",
                createTableNameDefinition(),
                createDefaultAllUpdateStatements(),
                createWhereSql());
    }

    private String createDefaultAllUpdateStatements() {
        return entityTable.getColumns()
                .stream()
                .filter(entityColumn -> !entityColumn.isIdColumn())
                .map(this::getEntityColumnUpdateStatement)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    private String getEntityColumnUpdateStatement(EntityColumn<E, ?> entityColumn) {
        String entityColumnName = createColumnNameDefinition(entityColumn);

        if (entityColumn.getType() == String.class) {
            return entityColumnName + " = " + ("'" + entityColumn.getValue(entity) + "'");
        }

        return entityColumnName + " = " + entityColumn.getValue(entity);
    }

    private String createWhereSql() {
        if (whereClause == null) {
            return "";
        }

        return new WhereClauseSQLBuilder(whereClause).build();
    }
}
