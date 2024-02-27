package persistence.sql.ddl.builder;

import persistence.sql.ddl.clause.Create;
import persistence.sql.ddl.dialect.Dialect;

public class CreateQueryBuilder implements QueryBuilder {
    private final Dialect dialect;

    public CreateQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public String generateSQL(final Class<?> clazz) {
        Create create = new Create(clazz, dialect);
        return String.format(
                "create table %s\n" +
                        "(\n" +
                        "%s\n" +
                        ");\n"
                , create.getTableName()
                , create.getColumns()
        );
    }
}
