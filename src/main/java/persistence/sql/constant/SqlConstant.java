package persistence.sql.constant;

public enum SqlConstant {

    EMPTY(""),
    BLANK(" "),
    COMMA(","),
    AND("AND");

    private final String value;

    SqlConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
