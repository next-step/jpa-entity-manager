package persistence.sql.common.meta;

enum ColumnType {
    INT(null),
    INTEGER(null),
    BIGINT(null),
    VARCHAR(255),
    NULL(null)
    ;

    private Integer defaultSize;

    ColumnType(Integer defaultSize) {
        this.defaultSize = defaultSize;
    }

    public static <T> ColumnType of(Class<T> tClass) {
        switch (tClass.getSimpleName()) {
            case "int":
                return INT;
            case "Integer":
                return INTEGER;
            case "Long":
                return BIGINT;
            case "String":
                return VARCHAR;
            default:
                return NULL;
        }
    }

    String getType() {
        String type = " " + this;
        if (defaultSize != null) {
            type = type + String.format("(%s)", this.defaultSize);
        }
        return type;
    }
}
