package database.mapping;

import database.dialect.Dialect;
import database.mapping.column.EntityColumn;
import database.mapping.column.FieldToEntityColumnConverter;
import database.mapping.column.PrimaryKeyEntityColumn;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColumnsMetadata {

    private final List<EntityColumn> allEntityColumns;
    private final EntityColumn primaryKey;
    private final List<EntityColumn> generalColumns;
    private final Map<String, Field> fieldByColumnNameMap;

    private ColumnsMetadata(List<EntityColumn> allEntityColumns, EntityColumn primaryKey,
                           List<EntityColumn> generalColumns,
                           Map<String, Field> fieldByColumnNameMap) {
        this.allEntityColumns = allEntityColumns;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
        this.fieldByColumnNameMap = fieldByColumnNameMap;
    }

    public static ColumnsMetadata fromClass(Class<?> clazz) {
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class)).collect(Collectors.toList());

        List<EntityColumn> allEntityColumns = fields.stream()
                .map(field -> new FieldToEntityColumnConverter(field).convert())
                .collect(Collectors.toList());

        EntityColumn primaryKey = allEntityColumns.stream()
                .filter(EntityColumn::isPrimaryKeyField)
                .findFirst().get();

        List<EntityColumn> generalColumns = allEntityColumns.stream()
                .filter(columnMetadata -> !columnMetadata.isPrimaryKeyField())
                .collect(Collectors.toList());

        // TODO: H2 에서는 ResultSet 에서 돌아온 결과의 컬럼명이 대문자로 구성되어 있어서, 쉬운 비교를 위해서 미리 변환해서 저장해둠.
        // dialect 마다 상황이 다를 수도 있음. (대소문자를 구별해서 nick_name과 NICK_NAME 을 다르게 처리하는 경우에는 에러 발생한다)
        Map<String, Field> fieldByColumnNameMap = allEntityColumns.stream()
                .collect(Collectors.toMap(entityColumn -> entityColumn.getColumnName().toUpperCase(), EntityColumn::getField));

        return new ColumnsMetadata(allEntityColumns, primaryKey, generalColumns, fieldByColumnNameMap);
    }

    public List<String> getAllColumnNames() {
        return allEntityColumns.stream().map(EntityColumn::getColumnName).collect(Collectors.toList());
    }

    public List<String> getColumnDefinitions(Dialect dialect) {
        return allEntityColumns.stream()
                .map(entityColumn -> entityColumn.toColumnDefinition(dialect))
                .collect(Collectors.toList());
    }

    public String getPrimaryKeyColumnName() {
        return primaryKey.getColumnName();
    }

    public List<String> getGeneralColumnNames() {
        return generalColumns.stream().map(EntityColumn::getColumnName).collect(Collectors.toList());
    }

    public List<EntityColumn> getGeneralColumns() {
        return generalColumns;
    }

    public Long getPrimaryKeyValue(Object entity) {
        return (Long) primaryKey.getValue(entity);
    }

    public Field getFieldByColumnName(String columnName) {
        String upperCase = columnName.toUpperCase();
        return fieldByColumnNameMap.get(upperCase);
    }

    public boolean hasIdGenerationStrategy() {
        return ((PrimaryKeyEntityColumn) primaryKey).hasIdGenerationStrategy();
    }
}
