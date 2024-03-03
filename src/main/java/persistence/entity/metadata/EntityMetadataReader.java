package persistence.entity.metadata;

import java.util.List;

public interface EntityMetadataReader {

    String getTableName(Class<?> clazz);
    String getIdColumnName(Class<?> clazz);
    List<EntityColumn> getInsertTargetColumns(Class<?> clazz);

    EntityColumn getEntityColumnByColumnName(Class<?> clazz, String columnName);

    List<EntityColumn> getColumns(Class<?> clazz);

}
