package persistence.sql.dml;

import persistence.sql.ddl.TableName;
import persistence.sql.ddl.ValidateEntity;
import persistence.sql.dml.querybuilder.QueryBuilder;

public class DeleteQuery implements SqlQuery {

    public String generateQuery(Object entity) {
        new ValidateEntity(entity.getClass());
        return new QueryBuilder()
            .delete()
            .from(new TableName(entity.getClass()).getTableName())
            .where("id = " + new EntityId(entity).getId())
            .build();
    }

}
