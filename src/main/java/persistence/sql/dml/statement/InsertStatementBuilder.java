package persistence.sql.dml.statement;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.ColumnMeta;
import persistence.sql.schema.EntityClassMappingMeta;
import persistence.sql.schema.EntityObjectMappingMeta;

public class InsertStatementBuilder {

    private static final String INSERT_FORMAT = "INSERT INTO %s (%s) values (%s)";
    private static final String INSERT_RETURNING_FORMAT = "SELECT * FROM FINAL TABLE (%s)";

    private final ColumnType columnType;

    public InsertStatementBuilder(ColumnType columnType) {
        this.columnType = columnType;
    }

    public String insert(Object object) {
        final EntityObjectMappingMeta entityObjectMappingMeta = EntityObjectMappingMeta.of(object, columnType);

        return String.format(INSERT_FORMAT,
            entityObjectMappingMeta.tableClause(),
            columnClause(entityObjectMappingMeta),
            valueClause(entityObjectMappingMeta)
        );
    }

    public String insertReturning(Object object) {
        final EntityObjectMappingMeta entityObjectMappingMeta = EntityObjectMappingMeta.of(object, columnType);

        final String insertSql = String.format(INSERT_FORMAT,
            entityObjectMappingMeta.tableClause(),
            columnClause(entityObjectMappingMeta),
            valueClause(entityObjectMappingMeta)
        );

        return String.format(INSERT_RETURNING_FORMAT, insertSql);
    }

    private String columnClause(EntityObjectMappingMeta entityObjectMappingMeta) {
        final List<String> columnNameList = entityObjectMappingMeta.getColumnMetaList().stream()
            .filter(columnMeta -> !columnMeta.isPrimaryKey())
            .map(ColumnMeta::getColumnName)
            .collect(Collectors.toList());

        return String.join(", ", columnNameList);
    }

    private String valueClause(EntityObjectMappingMeta entityObjectMappingMeta) {
        final List<String> formattedValueList = entityObjectMappingMeta.getMetaEntryList().stream()
            .filter(entry -> !entry.getKey().isPrimaryKey())
            .map(entry -> entry.getValue().getFormattedValue())
            .collect(Collectors.toList());

        return String.join(", ", formattedValueList);
    }
}
