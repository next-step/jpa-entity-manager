package persistence.entity.metadata;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import persistence.inspector.EntityFieldInspector;
import persistence.inspector.EntityInfoExtractor;
import persistence.sql.DataType;

public class EntityMetadataBuilder {

    static DataType dataType = new DataType();

    public static EntityMetadata build(Class<?> clazz) {
        EntityMetadata metadata = new EntityMetadata();
        metadata.setTableName(EntityInfoExtractor.getTableName(clazz));
        metadata.setColumns(buildColumns(clazz));

        return metadata;
    }

    private static List<EntityColumn> buildColumns(Class<?> clazz) {
        return EntityInfoExtractor.getColumns(clazz).stream()
                .map(EntityMetadataBuilder::buildColumn)
                .collect(Collectors.toList());
    }

    private static EntityColumn buildColumn(Field field) {
        EntityColumn column = new EntityColumn();
        column.setName(EntityInfoExtractor.getColumnName(field));
        column.setSqlTypeCode(dataType.getSqlTypeCode(field.getType()));
        column.setPrimaryKey(EntityInfoExtractor.isPrimaryKey(field));
        column.setNullable(EntityFieldInspector.isNullable(field));
        column.setGenerationType(EntityFieldInspector.getGenerationType(field));
        column.setLength(EntityFieldInspector.getLength(field));
        return column;
    }

}
