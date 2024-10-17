package persistence.dialect;

import java.sql.Types;

public class H2Dialect extends Dialect {
    public H2Dialect() {
        addType(Types.VARCHAR, "VARCHAR");
        addType(Types.INTEGER, "INTEGER");
        addType(Types.BIGINT, "BIGINT");
    }
}
