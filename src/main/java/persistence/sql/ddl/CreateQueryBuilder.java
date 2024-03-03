package persistence.sql.ddl;

import domain.EntityMetaData;
import domain.H2GenerationType;
import domain.dialect.Dialect;
import domain.vo.JavaMappingType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static domain.Constraints.NOT_NULL;
import static domain.Constraints.NULL;
import static domain.Constraints.PRIMARY_KEY;
import static domain.constants.CommonConstants.COMMA;

public class CreateQueryBuilder {

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE %s ( %s );";

    private final JavaMappingType javaMappingType;
    private final Dialect dialect;
    private final EntityMetaData entityMetaData;

    public CreateQueryBuilder(Dialect dialect, EntityMetaData entityMetaData) {
        this.javaMappingType = new JavaMappingType();
        this.dialect = dialect;
        this.entityMetaData = entityMetaData;
    }

    public String createTable(Object object) {
        return String.format(CREATE_TABLE_QUERY, entityMetaData.getTableName(), createClause(object.getClass()));
    }

    private String createClause(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !isTransientField(field))
                .map(this::getFieldInfo)
                .collect(Collectors.joining(COMMA))
                .replaceAll(",[\\s,]*$", "");
    }

    private boolean isTransientField(Field field) {
        return field.isAnnotationPresent(Transient.class);
    }

    private String getFieldInfo(Field field) {
        if (entityMetaData.isIdField(field)) {
            return Stream.of(
                            entityMetaData.getFieldName(field), getFieldType(field),
                            NOT_NULL.getName(), PRIMARY_KEY.getName(), getGenerationType(field))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
        }

        return Stream.of(
                        entityMetaData.getFieldName(field), getFieldType(field),
                        getFieldLength(field), getColumnNullConstraint(field))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    public String getFieldType(Field field) {
        Integer javaTypeByClass = javaMappingType.getJavaTypeByClass(field.getType());
        return dialect.getColumnDefine(javaTypeByClass);
    }

    public String getFieldLength(Field field) {
        return Objects.nonNull(getColumnLength(field)) ? "(" + getColumnLength(field) + ")" : null;
    }

    public String getGenerationType(Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return H2GenerationType.from(field.getAnnotation(GeneratedValue.class).strategy()).getStrategy();
        }
        return null;
    }

    public String getColumnNullConstraint(Field field) {
        if (!isColumnField(field) || field.getAnnotation(Column.class).nullable()) {
            return NULL.getName();
        }
        return NOT_NULL.getName();
    }

    private boolean isColumnField(Field field) {
        return field.isAnnotationPresent(Column.class);
    }

    private String getColumnLength(Field field) {
        if (isColumnField(field) && isVarcharType(field)) {
            return String.valueOf(field.getAnnotation(Column.class).length());
        }

        if (isColumnField(field) && !isVarcharType(field)) {
            return getLengthOrDefaultValue(field, 255);
        }

        return null;
    }

    private boolean isVarcharType(Field field) {
        Integer javaTypeByClass = javaMappingType.getJavaTypeByClass(field.getType());
        return javaTypeByClass.equals(Types.VARCHAR);
    }

    private String getLengthOrDefaultValue(Field field, int defaultLengthValue) {
        return field.getAnnotation(Column.class).length() == defaultLengthValue ? null
                : String.valueOf(field.getAnnotation(Column.class).length());
    }
}
