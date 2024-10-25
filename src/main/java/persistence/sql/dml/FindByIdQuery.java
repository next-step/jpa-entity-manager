package persistence.sql.dml;

import persistence.sql.ddl.TableName;
import persistence.sql.ddl.ValidateEntity;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class FindByIdQuery implements SqlQuery {
    private final Class<?> entityClass;

    public FindByIdQuery(Class<?> entityClass) {
        new ValidateEntity(entityClass);
        this.entityClass = entityClass;
    }

    public String generateQuery(Object id) {
        return new QueryBuilder()
            .select("*")
            .from(new TableName(entityClass).getTableName())
            .where("id = " + id)
            .build();
    }

}
