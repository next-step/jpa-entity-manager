package persistence.sql;

public class SQLEscaper {
    public static String escapeNameByBacktick(String name) {
        return "`" + name + "`";
    }

    public static String escapeNameBySingleQuote(String name) {
        return "'" + name + "'";
    }
}
