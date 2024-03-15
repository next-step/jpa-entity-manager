package persistence.sql.dml;

import pojo.EntityMetaData;
import pojo.FieldInfo;
import pojo.FieldInfos;

import java.lang.reflect.Field;
import java.util.List;

import static constants.CommonConstants.AND;
import static constants.CommonConstants.COMMA;
import static constants.CommonConstants.EQUAL;

public class UpdateQueryBuilder {

    private static final String INSERT_DATA_QUERY = "INSERT INTO %s (%s) VALUES (%s);";
    private static final String UPDATE_DATA_QUERY = "UPDATE %s SET %s WHERE %s;";

    private final EntityMetaData entityMetaData;

    public UpdateQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public String insertQuery(Object entity) {
        return String.format(INSERT_DATA_QUERY, entityMetaData.getTableInfo().getName(), columnsClause(entity), valuesClause(entity));
    }

    public String updateQuery(Object entity) {
        return String.format(UPDATE_DATA_QUERY, entityMetaData.getTableInfo().getName(), setClause(entity), whereClause(entity));
    }

    private String columnsClause(Object entity) {
        return new FieldInfos(entity.getClass().getDeclaredFields()).getIdAndColumnFields().stream()
                .map(field -> new FieldInfo(field, entity))
                .map(fieldInfo -> fieldInfo.getFieldName().getName())
                .reduce((o1, o2) -> String.join(COMMA, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));
    }

    private String valuesClause(Object entity) {
        return new FieldInfos(entity.getClass().getDeclaredFields()).getIdAndColumnFields().stream()
                .map(field -> new FieldInfo(field, entity))
                .map(fieldInfo -> fieldInfo.getFieldValue().getValue())
                .reduce((o1, o2) -> String.join(COMMA, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));
    }

    private String setClause(Object entity) {
        List<Field> columnFields = new FieldInfos(entity.getClass().getDeclaredFields()).getColumnFields();
        return fieldNameAndValueClause(entity, columnFields, COMMA);
    }

    private String whereClause(Object entity) {
        Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
        return fieldNameAndValueClause(entity, List.of(field), AND);
    }

    private String fieldNameAndValueClause(Object entity, List<Field> fields, String delimiter) {
        return fields.stream()
                .map(field -> new FieldInfo(field, entity))
                .filter(FieldInfo::isNotBlankOrEmpty)
                .map(fieldInfo -> fieldInfo.joinNameAndValueWithDelimiter(EQUAL))
                .reduce((o1, o2) -> String.join(delimiter, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("update 데이터가 없습니다."));
    }
}
