package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.*;

import java.util.stream.Collectors;

public class DdlCreateQueryBuilder {

    public static final String COMMA = ", ";
    private final TableName tableMeta;
    private final PrimaryKey primaryKey;
    private final Columns columns;
    private final Dialect dialect;

    public DdlCreateQueryBuilder(final EntityMetaCreator entityMetaCreator, final Dialect dialect) {
        this.tableMeta = entityMetaCreator.createTableName();
        this.primaryKey = entityMetaCreator.createPrimaryKey();
        this.columns = entityMetaCreator.createColumns();
        this.dialect = dialect;
    }

    public String createDdl() {
        return String.format(dialect.getCreateDefaultDdlQuery(), this.tableMeta.name(), createColumns());
    }

    private String createColumns() {
        return String.join(COMMA, primaryKeyColumns(), columns(), constraint());
    }

    private String primaryKeyColumns() {
        return DdlGenerator.createPrimaryKeyColumnsClause(this.primaryKey, dialect);
    }

    private String columns() {
        return this.columns.getColumns()
                .stream()
                .map(c-> createColumnsClause(c))
                .collect(Collectors.joining(", "));
    }

    private String constraint() {
        return DdlGenerator.constraint(this.primaryKey);
    }

    private String createColumnsClause(Column c) {
        return DdlGenerator.columns(c, this.dialect);
    }
}
