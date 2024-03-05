package persistence.sql.dml;

import domain.EntityMetaData;
import domain.H2GenerationType;
import domain.dialect.Dialect;
import domain.vo.ColumnName;
import domain.vo.ColumnNameAndValue;
import domain.vo.ColumnValue;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static domain.constants.CommonConstants.AND;
import static domain.constants.CommonConstants.COMMA;
import static domain.constants.CommonConstants.EQUAL;

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
        return String.format(INSERT_DATA_QUERY, entityMetaData.getTableName(), columnsClause(object), valuesClause(object));
    }

    public String updateQuery(Object object) {
        return String.format(UPDATE_DATA_QUERY, entityMetaData.getTableName(), setClause(object), whereClause(object));
    }

    private String columnsClause(Object object) {
        List<Field> fields = entityMetaData.getIdAndColumnFields(object);
        return fields.stream()
                .map(this::getColumnName)
                .map(ColumnName::getName)
                .reduce((o1, o2) -> String.join(COMMA, o1, String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."));
    }

    private String valuesClause(Object object) {
        return entityMetaData.getIdAndColumnFields(object).stream()
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        Object fieldValue = field.get(object);

                        //Id 필드 check
                        if (entityMetaData.isIdField(field)) {
                            isValidIdFieldValue(field, fieldValue);
                        }

                        //Column 필드, 어노테이션이 없는 필드
                        if (isColumnField(field)) {
                            isValidColumnFieldValue(field, fieldValue);
                        }

                        if (Objects.isNull(fieldValue)) {
                            return new ColumnValue(null);
                        }

                        String typeToStr = dialect.getTypeToStr(field.getType());
                        fieldValue = typeToStr.equals("varchar") ? "'" + fieldValue + "'" : fieldValue;
                        return new ColumnValue(fieldValue);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("필드 정보를 가져올 수 없습니다.");
                    }
                })
                .map(ColumnValue::getValue)
                .reduce((o1, o2) -> String.join(COMMA, String.valueOf(o1), String.valueOf(o2)))
                .orElseThrow(() -> new IllegalStateException("Id 혹은 Column 타입이 없습니다."))
                .toString();
    }

    private String setClause(Object object) {
        LinkedList<Field> columnFields = entityMetaData.getIdAndColumnFields(object).stream()
                .filter(field -> !entityMetaData.isIdField(field))
                .collect(Collectors.toCollection(LinkedList::new));

        return columnNameAndValueClause(columnFields, object, COMMA);
    }

    private String whereClause(Object object) {
        LinkedList<Field> idField = entityMetaData.getIdAndColumnFields(object).stream()
                .filter(entityMetaData::isIdField)
                .collect(Collectors.toCollection(LinkedList::new));

        return columnNameAndValueClause(idField, object, AND);
    }

    private String columnNameAndValueClause(LinkedList<Field> fields, Object object, String delimiter) {
        return fields.stream()
                .map(field -> new ColumnNameAndValue(getColumnName(field), getColumnValue(object, field)))
                .filter(ColumnNameAndValue::isNotBlankOrEmpty)
                .map(columnNameAndValue -> columnNameAndValue.joinNameAndValueWithDelimiter(EQUAL))
                .reduce((o1, o2) -> String.join(delimiter, o1, o2))
                .orElseThrow(() -> new IllegalStateException("update 데이터가 없습니다."));
    }

    private ColumnName getColumnName(Field field) {
        return new ColumnName(entityMetaData.getFieldName(field));
    }

    private ColumnValue getColumnValue(Object object, Field field) {
        field.setAccessible(true);
        try {
            Object fieldValue = field.get(object);

            if (Objects.nonNull(fieldValue)) {
                String typeToStr = dialect.getTypeToStr(field.getType());
                fieldValue = typeToStr.equals("varchar") ? "'" + fieldValue + "'" : fieldValue;
                return new ColumnValue(fieldValue);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("필드 정보를 가져올 수 없습니다.");
        }

        return null;
    }

    private boolean isColumnField(Field field) {
        return field.isAnnotationPresent(Column.class);
    }

    private void isValidIdFieldValue(Field field, Object fieldValue) {
        if (Objects.isNull(fieldValue) && !isGenerationTypeAutoOrIdentity(field)) {
            throw new IllegalArgumentException("fieldValue 가 null 이어서는 안됩니다.");
        }
    }

    //TODO 다른 GenerationType 에 대한 검증도 필요합니다. AUTO, IDENTITY 가 아닐 경우
    private boolean isGenerationTypeAutoOrIdentity(Field field) {
        H2GenerationType generationType = getGenerationType(field);
        return Objects.nonNull(generationType) && (generationType.equals(H2GenerationType.AUTO)
                || generationType.equals(H2GenerationType.IDENTITY));
    }

    private H2GenerationType getGenerationType(Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return H2GenerationType.from(field.getAnnotation(GeneratedValue.class).strategy());
        }
        return null;
    }

    //TODO 길이 체크 리팩토링이 필요한 부분
    private void isValidColumnFieldValue(Field field, Object fieldValue) {
        if (Objects.isNull(fieldValue) && !field.getAnnotation(Column.class).nullable()) {
            throw new IllegalArgumentException("fieldValue 가 null 이어서는 안됩니다.");
        }
    }
}
