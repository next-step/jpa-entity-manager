package persistence.sql.ddl;

import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class DropQueryBuilder {
    private static final String DROP_TABLE_COMMAND = "DROP TABLE %s IF EXISTS;";

    public DropQueryBuilder() {
    }

    public String buildQuery(EntityMetadata entityMetadata) {
        return format(DROP_TABLE_COMMAND, entityMetadata.getTableName());
    }
}
