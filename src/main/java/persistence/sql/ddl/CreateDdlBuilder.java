package persistence.sql.ddl;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.dialect.TypeJavaClassMappings;
import persistence.dialect.DdlType;
import persistence.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class CreateDdlBuilder {
    private final Dialect dialect;

    protected CreateDdlBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public String createTableBuild(Class<?> clazz) {
        return new StringBuilder()
                .append(dialect.getCreateTableString())
                .append(" ")
                .append(clazz.getSimpleName().toLowerCase())
                .append(" (")
                .append(idColumn(clazz.getDeclaredFields()))
                .append(columns(clazz.getDeclaredFields()))
                .append(",")
                .append(primaryKey(clazz.getDeclaredFields()))
                .append(")")
                .toString();
    }

    private String primaryKey(Field[] fields) {
        return Arrays.stream(fields)
                .filter(this::unique)
                .map(field -> dialect.getPrimaryKeyStrategy(columnName(field)))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private String idColumn(Field[] fields) {
        return Arrays.stream(fields)
                .filter(this::unique)
                .map(field -> column(field) + dialect.getNativeIdentifierGeneratorStrategy() + ", ")
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private String columns(Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> this.notTransient(field) && this.notUnique(field))
                .map(field -> this.columnBuild(field) + this.columnNotNullBuild(field))
                .collect(Collectors.joining(", "));
    }

    private String columnNotNullBuild(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null && !columnAnnotation.nullable()) {
            return " not null";
        }

        return "";
    }

    private boolean unique(Field field) {
        Id annotation = field.getAnnotation(Id.class);

        return annotation != null;
    }

    private boolean notUnique(Field field) {
        return !unique(field);
    }

    private String columnBuild(Field field) {
        return new DdlType(column(field))
                .getTypeName(columnSize(field));
    }

    private Long columnSize(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        if (columnAnnotation == null) {
            return -1L;
        }

        return (long) columnAnnotation.length();
    }

    private String column(Field field) {
        return columnName(field) + " " + dialect.columnType(TypeJavaClassMappings.INSTANCE.valueOf(field.getType()));
    }

    private static String columnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        String name = field.getName();
        if (columnAnnotation != null && !columnAnnotation.name().equals("")) {
            name = columnAnnotation.name();
        }
        return name;
    }


    private boolean notTransient(Field field) {
        Transient annotation = field.getAnnotation(Transient.class);

        return annotation == null;
    }
}
