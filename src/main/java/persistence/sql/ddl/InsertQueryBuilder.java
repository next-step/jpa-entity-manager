package persistence.sql.ddl;

public abstract class InsertQueryBuilder {

    protected InsertQueryBuilder() {
    }

    public String createInsertBuild(Object object) {
        ColumnMap columnMap = ColumnMap.of(object);
        return String.format("insert into %s (%s) values (%s)", object.getClass().getSimpleName(), columnMap.names(), columnMap.values());
    }
}
