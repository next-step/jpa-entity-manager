package persistence.sql.dml;

import domain.EntityMetaData;
import domain.vo.ColumnName;
import domain.vo.ColumnValue;
import domain.vo.JavaMappingType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class DeleteQueryBuilder {

    private static final String DELETE_QUERY = "DELETE %s WHERE %s = %s;";

    private final JavaMappingType javaMappingType;
    private final EntityMetaData entityMetaData;

    public DeleteQueryBuilder(JavaMappingType javaMappingType, EntityMetaData entityMetaData) {
        this.javaMappingType = javaMappingType;
        this.entityMetaData = entityMetaData;
    }

    public String deleteQuery(Object object, Field field, Object value) {
        LinkedList<Field> fields = Arrays.stream(object.getClass().getDeclaredFields())
                .collect(Collectors.toCollection(LinkedList::new));

        ColumnName columnName = new ColumnName(fields, field);
        ColumnValue columnValue = new ColumnValue(javaMappingType, javaMappingType.getJavaTypeByClass(object.getClass()), value);

        return String.format(DELETE_QUERY, entityMetaData.getTableName(), columnName.getName(), columnValue.getValue());
    }

    public String deleteByIdQuery(Object object) {
        Field idField = Arrays.stream(object.getClass().getDeclaredFields())
                .collect(Collectors.toCollection(LinkedList::new)).stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("primary key 값이 없습니다."));

        Object idFieldValue;
        try {
            idField.setAccessible(true);
            idFieldValue = idField.get(object);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Field 정보가 존재하지 않습니다.");
        }

        return deleteQuery(object, idField, idFieldValue);
    }
}
