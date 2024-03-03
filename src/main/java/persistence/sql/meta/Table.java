package persistence.sql.meta;

public interface Table {
    String name();
    PrimaryKey primaryKey();
    Columns columns();
}
