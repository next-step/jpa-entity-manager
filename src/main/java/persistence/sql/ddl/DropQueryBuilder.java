package persistence.sql.ddl;

import persistence.sql.EntityMetadata;

public class DropQueryBuilder {

    public String getDropTableQuery(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "DROP TABLE %s",
            entityMetadata.getTableName()
        );
    }
}
