package persistence.sql.ddl;

import java.util.List;
import java.util.stream.Collectors;

import persistence.core.EntityMetaManager;
import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityMetadata;
import persistence.sql.Dialect;

public abstract class DDLSqlGenerator {

    protected Dialect dialect;
    private final static String CREATE_TABLE_QUERY_FORMAT = "CREATE TABLE %s (%s)";
    private final static String DROP_TABLE_QUERY_FORMAT = "DROP TABLE %s";


    protected DDLSqlGenerator(Dialect dialect) {
        this.dialect = dialect;
    }

    public String genCreateTableQuery(String tableName, List<EntityColumn> columns) {
        String columnClause = createColumnClause(columns);

        return String.format(CREATE_TABLE_QUERY_FORMAT, tableName, columnClause);
    }

    public String genDropTableQuery(String tableName) {
        return String.format(DROP_TABLE_QUERY_FORMAT, tableName);
    }

    protected String createColumnClause(List<EntityColumn> columns) {
        List<String> collect = columns.stream()
                .map(this::getColumnClause)
                .collect(Collectors.toList());

        return String.join(", ", collect);
    }

    protected abstract String getColumnClause(EntityColumn entityColumn);


}
