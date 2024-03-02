package database.mapping;

import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.*;

public class SqlTypes {

    private final Map<String, Integer> nameToCode;
    private final Map<Integer, String> codeToName;

    public SqlTypes() {
        nameToCode = new HashMap<>();
        codeToName = new HashMap<>();

        register(BIT, "BIT");
        register(TINYINT, "TINYINT");
        register(SMALLINT, "SMALLINT");
        register(INTEGER, "INT");
        register(BIGINT, "BIGINT");
        register(FLOAT, "FLOAT");
        register(REAL, "REAL");
        register(DOUBLE, "DOUBLE");
        register(NUMERIC, "NUMERIC");
        register(DECIMAL, "DECIMAL");
        register(CHAR, "CHAR");
        register(VARCHAR, "VARCHAR");
        register(LONGVARCHAR, "LONGVARCHAR");
        register(DATE, "DATE");
        register(TIME, "TIME");
        register(TIMESTAMP, "TIMESTAMP");
        register(BINARY, "BINARY");
        register(VARBINARY, "VARBINARY");
        register(LONGVARBINARY, "LONGVARBINARY");
        register(NULL, "NULL");
        register(OTHER, "OTHER");
        register(JAVA_OBJECT, "JAVA_OBJECT");
        register(DISTINCT, "DISTINCT");
        register(STRUCT, "STRUCT");
        register(ARRAY, "ARRAY");
        register(BLOB, "BLOB");
        register(CLOB, "CLOB");
        register(REF, "REF");
        register(DATALINK, "DATALINK");
        register(BOOLEAN, "BOOLEAN");
        register(ROWID, "ROWID");
        register(NCHAR, "NCHAR");
        register(NVARCHAR, "NVARCHAR");
        register(LONGNVARCHAR, "LONGNVARCHAR");
        register(NCLOB, "NCLOB");
        register(SQLXML, "SQLXML");
        register(REF_CURSOR, "REF_CURSOR");
        register(TIME_WITH_TIMEZONE, "TIME_WITH_TIMEZONE");
        register(TIMESTAMP_WITH_TIMEZONE, "TIMESTAMP_WITH_TIMEZONE");

    }

    public String codeToName(int i) {
        return codeToName.get(i);
    }

    private void register(int code, String name) {
        nameToCode.put(name, code);
        codeToName.put(code, name);
    }
}
