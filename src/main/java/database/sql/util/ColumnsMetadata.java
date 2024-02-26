package database.sql.util;

import database.sql.util.column.EntityColumn;
import database.sql.util.column.FieldToEntityColumnConverter;
import database.sql.util.type.TypeConverter;
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

    public ColumnsMetadata(Class<?> entityClass) {
        allEntityColumns = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(field -> new FieldToEntityColumnConverter(field).convert())
                .collect(Collectors.toList());

        primaryKey = allEntityColumns.stream()
                .filter(EntityColumn::isPrimaryKeyField).findFirst().get();

        generalColumns = allEntityColumns.stream()
                .filter(columnMetadata -> !columnMetadata.isPrimaryKeyField())
                .collect(Collectors.toList());

        // TODO: H2 에서는 ResultSet 에서 돌아온 결과의 컬럼명이 대문자로 구성되어 있어서, 쉬운 비교를 위해서 미리 변환해서 저장해둠.
        // dialect 마다 상황이 다를 수도 있음. (대소문자를 구별해서 nick_name과 NICK_NAME 을 다르게 처리하는 경우에는 에러 발생한다)
        fieldByColumnNameMap = allEntityColumns.stream()
                .collect(Collectors.toMap(entityColumn -> entityColumn.getColumnName().toUpperCase(),
                                          EntityColumn::getField));
    }

    public List<String> getAllColumnNames() {
        return allEntityColumns.stream().map(EntityColumn::getColumnName).collect(Collectors.toList());
    }

    public List<String> getColumnDefinitions(TypeConverter typeConverter) {
        return allEntityColumns.stream()
                .map(entityColumn -> entityColumn.toColumnDefinition(typeConverter))
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
}
