package persistence.sql.meta;

public interface EntityMetaCreator {
    String createTableName();
    PrimaryKey createPrimaryKey();
    Columns createColumns();
}
