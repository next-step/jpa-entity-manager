package database.sql.dml.where;

enum OperatorType {
    IS("IS"),
    IN("IN"),
    EQUAL("=");

    private final String symbol;

    OperatorType(String symbol) {
        this.symbol = symbol;
    }

    public static OperatorType examineType(ValueType valueType) {
        switch (valueType) {
            case NULL:
                return IS;
            case LIST:
                return IN;
        }
        return EQUAL;
    }

    public String toQuery() {
        return symbol;
    }
}
