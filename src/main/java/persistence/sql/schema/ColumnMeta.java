package persistence.sql.schema;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.constraint.Constraint;
import persistence.sql.schema.constraint.NotNullConstraint;
import persistence.sql.schema.constraint.PrimaryKeyConstraint;

public class ColumnMeta {

    private static final String COLUMN_FORMAT = "%s %s";
    private static final String COLUMN_FORMAT_WITH_CONSTRAINT = "%s %s %s";

    private final String columnName;

    private final String columnType;

    private final String columnConstraint;

    private final boolean isPrimaryKey;

    private final Class<?> columnTypeClass;

    private ColumnMeta(String columnName, String columnTypeName, String columnConstraint, boolean isPrimaryKey, Class<?> columnTypeClass) {
        this.columnName = columnName;
        this.columnType = columnTypeName;
        this.columnConstraint = columnConstraint;
        this.isPrimaryKey = isPrimaryKey;
        this.columnTypeClass = columnTypeClass;
    }

    public static ColumnMeta of(Field field, ColumnType columnType) {
        return new ColumnMeta(
            getColumnName(field),
            columnType.getFieldType(field),
            joiningConstraint(
                new PrimaryKeyConstraint(field, columnType), new NotNullConstraint(field)
            ),
            PrimaryKeyConstraint.isPrimaryKey(field),
            field.getType()
        );
    }

    public String getColumn() {
        if (columnConstraint.isEmpty()) {
            return String.format(COLUMN_FORMAT, columnName, columnType);
        }

        return String.format(COLUMN_FORMAT_WITH_CONSTRAINT, columnName, columnType, columnConstraint);
    }

    public String getColumnName() {
        return columnName;
    }

    public Class<?> getColumnType() {
        return columnTypeClass;
    }

    public static boolean isTransient(Field field) {
        return field.isAnnotationPresent(Transient.class);
    }

    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    private static String getColumnName(Field field) {
        if (!hasColumnAnnotationPresent(field)) {
            return field.getName().toLowerCase();
        }

        if (hasColumnAnnotationPresent(field) && field.getAnnotation(Column.class).name().isEmpty()) {
            return field.getName().toLowerCase();
        }

        return field.getAnnotation(Column.class).name();
    }

    private static boolean hasColumnAnnotationPresent(Field field) {
        return field.isAnnotationPresent(Column.class);
    }

    private static String joiningConstraint(Constraint... constraintArray) {
        return Stream.of(constraintArray)
            .map(Constraint::getConstraint)
            .filter(constraint -> !constraint.isEmpty())
            .collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColumnMeta that = (ColumnMeta) o;
        return Objects.equals(columnName, that.columnName) && Objects.equals(columnType, that.columnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName, columnType);
    }
}
