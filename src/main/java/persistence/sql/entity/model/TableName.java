package persistence.sql.entity.model;

public class TableName {

    private final String name;

    public TableName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
