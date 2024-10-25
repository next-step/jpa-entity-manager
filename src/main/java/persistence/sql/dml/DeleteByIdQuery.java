package persistence.sql.dml;

import persistence.sql.ddl.TableName;
import persistence.sql.ddl.ValidateEntity;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class DeleteByIdQuery implements SqlQuery {

    private final Class<?> entityClass;

    public DeleteByIdQuery(Class<?> entityClass) {
        new ValidateEntity(entityClass);
        this.entityClass = entityClass;
    }

    public String generateQuery(Object id) {
        return new QueryBuilder()
            .delete()
            .from(new TableName(entityClass).getTableName())
            .where("id = " + id)
            .build();
    }

}
