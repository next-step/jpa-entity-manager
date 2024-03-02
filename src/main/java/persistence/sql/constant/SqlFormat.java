package persistence.sql.constant;

public enum SqlFormat {

    STRING_FORMAT("'%s'"),
    UPDATE_COLUMN("%s=%s"),
    CREATE("CREATE TABLE %s(\n%s\n);"),
    DROP("DROP TABLE IF EXISTS %s;"),
    DELETE("DELETE FROM %s %s"),
    INSERT("INSERT INTO %s (%s) VALUES (%s)"),
    SELECT("SELECT %s FROM %s %s"),
    UPDATE("UPDATE %s SET %s %s"),
    WHERE("WHERE %s")
    ;

    private final String format;

    SqlFormat(final String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
