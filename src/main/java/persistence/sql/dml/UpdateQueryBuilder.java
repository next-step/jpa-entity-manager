package persistence.sql.dml;

import dialect.Dialect;
import pojo.EntityMetaData;
import pojo.FieldInfo;
import pojo.FieldInfos;
import pojo.FieldName;
import pojo.FieldNameAndValue;
import pojo.FieldValue;

import java.util.List;

import static constants.CommonConstants.AND;
import static constants.CommonConstants.COMMA;
import static constants.CommonConstants.EQUAL;

public class UpdateQueryBuilder {

    private static final String INSERT_DATA_QUERY = "INSERT INTO %s (%s) VALUES (%s);";
    private static final String UPDATE_DATA_QUERY = "UPDATE %s SET %s WHERE %s;";

    private final Dialect dialect;
    private final EntityMetaData entityMetaData;

    public UpdateQueryBuilder(Dialect dialect, EntityMetaData entityMetaData) {
        this.dialect = dialect;
        this.entityMetaData = entityMetaData;
    }

    public String insertQuery(Object object) {
        return String.format(INSERT_DATA_QUERY, entityMetaData.getTableInfo().getName(), columnsClause(object), valuesClause(object));
    }

    public String updateQuery(Object object) {
        return String.format(UPDATE_DATA_QUERY, entityMetaData.getTableInfo().getName(), setClause(object), whereClause(object));
    }

    private String columnsClause(Object object) {
        return new FieldInfos(object.getClass().getDeclaredFields()).getIdAndColumnFieldsData().stream()
                .map(fieldData -> new FieldName(fieldData.getField()))
                .map(FieldName::getName)
                .reduce((o1, o2) -> String.join(COMMA, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));
    }

    private String valuesClause(Object object) {
        return new FieldInfos(object.getClass().getDeclaredFields()).getIdAndColumnFieldsData().stream()
                .map(fieldData -> new FieldValue(dialect, fieldData.getField(), object))
                .map(FieldValue::getValue)
                .reduce((o1, o2) -> String.join(COMMA, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));
    }

    private String setClause(Object object) {
        List<FieldInfo> columnFieldsData = new FieldInfos(object.getClass().getDeclaredFields()).getColumnFieldsData();
        return fieldNameAndValueClause(columnFieldsData, object, COMMA);
    }

    private String whereClause(Object object) {
        FieldInfo idFieldInfo = new FieldInfos(object.getClass().getDeclaredFields()).getIdFieldData();
        return fieldNameAndValueClause(List.of(idFieldInfo), object, AND);
    }

    private String fieldNameAndValueClause(List<FieldInfo> fieldInfoList, Object object, String delimiter) {
        return fieldInfoList.stream()
                .map(FieldInfo::getField)
                .map(field -> new FieldNameAndValue(new FieldName(field), new FieldValue(dialect, field, object)))
                .filter(FieldNameAndValue::isNotBlankOrEmpty)
                .map(fieldNameAndValue -> fieldNameAndValue.joinNameAndValueWithDelimiter(EQUAL))
                .reduce((o1, o2) -> String.join(delimiter, o1, o2))
                .orElseThrow(() -> new IllegalStateException("update 데이터가 없습니다."));
    }
}
