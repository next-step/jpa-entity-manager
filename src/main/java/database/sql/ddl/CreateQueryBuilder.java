package database.sql.ddl;

import database.sql.util.EntityMetadata;
import database.sql.util.type.TypeConverter;

public class CreateQueryBuilder {
    private final String tableName;
    private final String columnsWithDefinition;

    public CreateQueryBuilder(EntityMetadata entityMetadata, TypeConverter typeConverter) {
        this.tableName = entityMetadata.getTableName();
        this.columnsWithDefinition = String.join(", ", entityMetadata.getColumnDefinitions(typeConverter));
    }

    public CreateQueryBuilder(Class<?> entityClass, TypeConverter typeConverter) {
        this(new EntityMetadata(entityClass), typeConverter);
    }

    public String buildQuery() {
        return String.format("CREATE TABLE %s (%s)", tableName, columnsWithDefinition);
    }
}
