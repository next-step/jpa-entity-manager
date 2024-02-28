package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.TableName;

public class InsertQueryBuilder {

    public static final String COMMA = ", ";
    private final TableName tableName;
    private final Columns columns;
    private final Dialect dialect;

    public InsertQueryBuilder(EntityMetaCreator entityMetaCreator, Dialect dialect) {
        this.tableName = entityMetaCreator.createTableName();
        this.columns = entityMetaCreator.createColumns();
        this.dialect = dialect;
    }

    // insert into %s (%s) values (%s)
    public String createInsertQuery(Object object) {
        return String.format(dialect.getInsertDefaultDmlQuery(), tableName(), insertColumns(), insertValues(object));
    }

    private String tableName() {
        return tableName.name();
    }

    private String insertColumns() {
        return String.join(COMMA, this.columns.names());
    }

    private String insertValues(Object object) {
        return String.join(COMMA, this.columns.values(object));
    }
}
