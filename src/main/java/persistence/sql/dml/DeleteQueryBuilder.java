package persistence.sql.dml;

import domain.EntityMetaData;
import domain.vo.ColumnName;
import domain.vo.ColumnNameAndValue;
import domain.vo.ColumnValue;
import jakarta.persistence.Id;

import java.lang.reflect.Field;

import static domain.constants.CommonConstants.EQUAL;

public class DeleteQueryBuilder {

    private static final String DELETE_QUERY = "DELETE %s WHERE %s;";

    private final EntityMetaData entityMetaData;

    public DeleteQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public String deleteQuery(Field field, Object value) {
        ColumnNameAndValue columnNameAndValue = new ColumnNameAndValue(new ColumnName(entityMetaData.getFieldName(field)),
                new ColumnValue(value));
        return String.format(DELETE_QUERY, entityMetaData.getTableName(), columnNameAndValue.joinNameAndValueWithDelimiter(EQUAL));
    }

    public String deleteByIdQuery(Object object) {
        Field idField = entityMetaData.getIdAndColumnFields(object).stream()
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

        return deleteQuery(idField, idFieldValue);
    }
}
