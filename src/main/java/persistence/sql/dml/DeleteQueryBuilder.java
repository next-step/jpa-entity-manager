package persistence.sql.dml;

import persistence.sql.QueryBuilder;
import persistence.sql.ddl.domain.Table;

import java.util.List;

public class DeleteQueryBuilder implements QueryBuilder {

    private static final String DELETE_QUERY = "DELETE FROM %s%s;";

    private final Table table;
    private final WhereQueryBuilder whereQueryBuilder;

    public DeleteQueryBuilder(Class<?> clazz, List<String> whereColumns, List<Object> whereValues) {
        this.table = new Table(clazz);
        this.whereQueryBuilder = new WhereQueryBuilder(clazz, whereColumns, whereValues);
    }

    public DeleteQueryBuilder(Object object) {
        Class<?> clazz = object.getClass();
        this.table = new Table(clazz);
        this.whereQueryBuilder = new WhereQueryBuilder(object);
    }

    @Override
    public String build() {
        return String.format(
                DELETE_QUERY,
                table.getName(),
                whereQueryBuilder.build()
        );
    }
}
