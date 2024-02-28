package persistence.sql.meta;

public interface EntityMetaCreator {
    TableName createTableName();
    PrimaryKey createPrimaryKey();
    Columns createColumns();
}
