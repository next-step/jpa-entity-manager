package persistence.sql.ddl;

import persistence.dialect.Dialect;
import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class CreateQueryBuilder {
    private static final String CREATE_TABLE_COMMAND = "CREATE TABLE %s";

    private final Dialect dialect;

    public CreateQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public String buildQuery(EntityMetadata entityMetadata) {
        return format(CREATE_TABLE_COMMAND, entityMetadata.getTableName()) +
                "(" +
                entityMetadata.getColumnsToCreate(dialect) +
                ");";
    }
}
