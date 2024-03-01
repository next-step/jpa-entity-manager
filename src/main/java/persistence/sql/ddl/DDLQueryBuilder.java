package persistence.sql.ddl;

public class DDLQueryBuilder {
    private final DDLSqlGenerator ddlSqlGenerator;

    public DDLQueryBuilder(DDLSqlGenerator ddlSqlGenerator) {
        this.ddlSqlGenerator = ddlSqlGenerator;
    }

    public String createTableQuery(Class<?> clzz) {
        return ddlSqlGenerator.genCreateTableQuery(clzz);
    }

    public String dropTableQuery(Class<?> clzz) {
        return ddlSqlGenerator.genDropTableQuery(clzz);
    }

}
