package persistence.sql.dml;

import persistence.sql.ddl.TableName;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class FindQuery {
    private final Class<?> entityClass;

    public FindQuery(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String generateFindByIdQuery(Object id) {
        return new QueryBuilder()
            .select("*")
            .from(new TableName(entityClass).getTableName())
            .where("id = " + id)
            .build();
    }

    public String generateFindAllQuery() {
        return new QueryBuilder()
            .select("*")
            .from(new TableName(entityClass).getTableName())
            .build();
    }
}
