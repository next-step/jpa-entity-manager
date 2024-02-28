package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.TableName;

public class DdlCreateQueryBuilder {

    private static final String CREATE_DEFAULT_DDL = "create table %s (%s)";
    public static final String COMMA = ", ";
    private final TableName tableMeta;
    private final CreateClause createClause;

    public DdlCreateQueryBuilder(final EntityMetaCreator entityMetaCreator, final Dialect dialect) {
        this.tableMeta = entityMetaCreator.createTableName();
        this.createClause = new CreateClause(entityMetaCreator, dialect);
    }

    public String createDdl() {
        return String.format(CREATE_DEFAULT_DDL, this.tableMeta.name(), createColumns());
    }

    private String createColumns() {
        return String.join(COMMA, this.createClause.primaryKeyClause(), this.createClause.columnClause(),
                this.createClause.constraintClause());
    }
}
