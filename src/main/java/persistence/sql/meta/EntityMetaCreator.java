package persistence.sql.meta;

public interface EntityMetaCreator {
    String name();
    PrimaryKey primaryKey();
    Columns columns();
}
