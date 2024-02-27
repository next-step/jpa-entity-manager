package persistence.sql.ddl;

import java.util.List;
import java.util.stream.Collectors;
import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityMetadata;
import persistence.sql.Dialect;

public abstract class DDLQueryGenerator {
    protected Dialect dialect;
    private final static String CREATE_TABLE_QUERY_FORMAT = "CREATE TABLE %s (%s)";
    private final static String DROP_TABLE_QUERY_FORMAT = "DROP TABLE %s";
    public String createTableQuery(EntityMetadata entityMetadata) {

        String tableName = entityMetadata.getTableName();
        String columnClause = createColumnClause(entityMetadata);

        return String.format(CREATE_TABLE_QUERY_FORMAT, tableName, columnClause);
    }

    public String createDropQuery(EntityMetadata entityMetadata) {
        return String.format(DROP_TABLE_QUERY_FORMAT, entityMetadata.getTableName());
    }

    protected String createColumnClause(EntityMetadata entityMetadata) {
        List<String> collect = entityMetadata.getColumns().stream()
            .map(this::getColumnClause)
            .collect(Collectors.toList());

        return String.join(", ", collect);
    }

    protected abstract String getColumnClause(EntityColumn entityColumn);



}
