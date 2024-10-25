package persistence.sql.dml;

import persistence.sql.ddl.TableName;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class FindByIdQuery {
    private final Class<?> entityClass;

    public FindByIdQuery(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String generateFindByIdQuery(Object id) {
        return new QueryBuilder()
            .select("*")
            .from(new TableName(entityClass).getTableName())
            .where("id = " + id)
            .build();
    }

}
