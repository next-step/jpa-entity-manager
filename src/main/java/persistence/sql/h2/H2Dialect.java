package persistence.sql.h2;

import persistence.sql.Dialect;

import java.sql.Types;

public class H2Dialect extends Dialect{

    public H2Dialect() {
        registerColumnType();
    }

    private void registerColumnType(){
        System.out.println("registerColumnType :: ==========================================");
        registerColumnType(Types.VARCHAR, "VARCHAR");
        registerColumnType(Types.INTEGER, "INT");
        registerColumnType(Types.BIGINT, "BIGINT");

        System.out.println("registerColumnType :: " + getDataType(Types.VARCHAR));
    }

}
