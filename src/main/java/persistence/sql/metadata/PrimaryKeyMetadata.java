package persistence.sql.metadata;

public class PrimaryKeyMetadata {

    private final String name;
    private final Object value;

    private PrimaryKeyMetadata(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public static PrimaryKeyMetadata of(ColumnMetadata column) {
        return new PrimaryKeyMetadata(column.getName(), column.getValue());
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
