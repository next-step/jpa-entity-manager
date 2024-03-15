package persistence.sql.metadata;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EntityMetadata {
    private final Class<?> clazz;
    private final String name;
    private final ColumnsMetadata columns;
    private final PrimaryKeyMetadata primaryKey;


    private EntityMetadata(Class<?> clazz, String name, ColumnsMetadata columns, PrimaryKeyMetadata primaryKey) {
        this.clazz = clazz;
        this.name = name;
        this.columns = columns;
        this.primaryKey = primaryKey;
    }

    public static EntityMetadata from(Class<?> clazz) {
        return new EntityMetadata(clazz, generateTableName(clazz), ColumnsMetadata.of(createColumns(clazz, null)), PrimaryKeyMetadata.of(generatePrimaryKey(clazz, null)));
    }

    public static EntityMetadata of(Class<?> clazz, Object object) {
        return new EntityMetadata(clazz, generateTableName(clazz), ColumnsMetadata.of(createColumns(clazz, object)), PrimaryKeyMetadata.of(generatePrimaryKey(clazz, object)));
    }

    private static String generateTableName(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(jakarta.persistence.Entity.class)) {
            throw new IllegalArgumentException("This is not an entity annotation.");
        }

        Table annotation = clazz.getDeclaredAnnotation(Table.class);

        if (Objects.nonNull(annotation) && !annotation.name().isEmpty()) {
            return annotation.name();
        }

        return convertCamelCaseToSnakeCase(clazz.getSimpleName());
    }

    protected static ColumnMetadata generatePrimaryKey(Class<?> clazz, Object entity) {
        return createColumns(clazz, entity).stream()
                .filter(column -> column.getAnnotations().stream()
                        .anyMatch(annotation -> annotation.annotationType().equals(Id.class)))
                .findFirst()
                .orElse(null);
    }

    public static List<ColumnMetadata> createColumns(Class<?> clazz, Object entity) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(field -> Objects.nonNull(entity) ? ColumnMetadata.of(field, entity) : ColumnMetadata.of(field))
                .collect(Collectors.toList());
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public List<ColumnMetadata> getColumns() {
        return columns.getColumns();
    }

    public ColumnsMetadata getColumnsMetadata() {
        return columns;
    }

    public PrimaryKeyMetadata getPrimaryKey() {
        return primaryKey;
    }

    private static String convertCamelCaseToSnakeCase(String input) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        String result = matcher.replaceAll(replacement);

        return result.toLowerCase();
    }
}
