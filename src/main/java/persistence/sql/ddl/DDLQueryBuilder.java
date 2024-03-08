package persistence.sql.ddl;

import persistence.core.EntityMetaManager;
import persistence.entity.metadata.EntityMetadata;

public class DDLQueryBuilder {
    private final DDLSqlGenerator ddlSqlGenerator;

    public DDLQueryBuilder(DDLSqlGenerator ddlSqlGenerator) {
        this.ddlSqlGenerator = ddlSqlGenerator;
    }

    public String createTableQuery(EntityMetadata entityMetadata) {
        return ddlSqlGenerator.genCreateTableQuery(entityMetadata.getTableName(), entityMetadata.getColumns().getColumns());
    }

    public String dropTableQuery(EntityMetadata entityMetadata) {
        return ddlSqlGenerator.genDropTableQuery(entityMetadata.getTableName());
    }

}
