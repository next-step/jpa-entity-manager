package persistence.sql.dml;

import java.util.Map;
import static persistence.sql.constant.SqlConstant.EQUALS;
import static persistence.sql.constant.SqlConstant.SPACE;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class DmlGenerator {

    private final SelectQueryBuilder selectQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    private static final String WHERE_CLAUSE = "WHERE";
    private static final String AND = "AND";

    private DmlGenerator() {
        this.selectQueryBuilder = SelectQueryBuilder.getInstance();
        this.insertQueryBuilder = InsertQueryBuilder.getInstance();
        this.updateQueryBuilder = UpdateQueryBuilder.getInstance();
        this.deleteQueryBuilder = DeleteQueryBuilder.getInstance();
    }

    private static class Holder {
        private static final DmlGenerator INSTANCE = new DmlGenerator();
    }

    public static DmlGenerator getInstance() {
        return Holder.INSTANCE;
    }

    public String generateInsertQuery(Object object) {
        Table table = getTable(object.getClass());
        return insertQueryBuilder.generateQuery(table, object);
    }

    public String generateSelectQuery(Class<?> clazz) {
        Table table = getTable(clazz);
        return selectQueryBuilder.generateQuery(table);
    }

    public String generateSelectQuery(Class<?> clazz, Object id) {
        Table table = getTable(clazz);
        return selectQueryBuilder.generateQuery(table) +
            whereClause(Map.of(table.getIdColumn(), id));
    }

    public String generateUpdateQuery(Object object) {
        Table table = getTable(object.getClass());
        return updateQueryBuilder.generateQuery(table, object) +
            whereClause(Map.of(table.getIdColumn(), table.getIdValue(object)));
    }

    public String generateDeleteQuery(Object object) {
        Table table = Table.getInstance(object.getClass());
        return deleteQueryBuilder.generateQuery(table) +
            whereClause(Map.of(table.getIdColumn(), table.getIdValue(object)));
    }

    private String whereClause(Map<Column, Object> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return "";
        }

        StringBuilder whereClause = new StringBuilder(SPACE.getValue() + WHERE_CLAUSE + SPACE.getValue());

        conditions.forEach((key, value) -> {
                whereClause.append(key.getColumnName());
                whereClause.append(SPACE.getValue());
                whereClause.append(EQUALS.getValue());
                whereClause.append(SPACE.getValue());
                whereClause.append(value);
                whereClause.append(SPACE.getValue());
                whereClause.append(AND);
                whereClause.append(SPACE.getValue());
        });

        whereClause.setLength(whereClause.length() - AND.length() - 1);

        return whereClause.toString();
    }

    private Table getTable(Class<?> clazz) {
        return Table.getInstance(clazz);
    }
}
