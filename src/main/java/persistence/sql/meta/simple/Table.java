package persistence.sql.meta.simple;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Table implements EntityMetaCreator {

    private final String tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;

    private Table(Class<?> clazz) {
        this.tableName = createTableName(clazz);
        this.primaryKey = createPrimaryKey(clazz);
        this.columns = createColumn(clazz);
    }

    private Table(Object object) {
        this.tableName = createTableName(object.getClass());
        this.primaryKey = createPrimaryKeyValue(object);
        this.columns = createColumnValues(object);
    }

    public static Table ofClass(Class<?> clazz) {
        return new Table(clazz);
    }

    public static Table ofInstance(Object object) {
        return new Table(object);
    }

    @Override
    public String name() {
        return this.tableName;
    }

    private String createTableName(final Class<?> clazz) {
        if (isNotEntityAnnotationPresent(clazz)) {
            throw new IllegalStateException();
        }

        if (clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
            return clazz.getAnnotation(jakarta.persistence.Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    private boolean isNotEntityAnnotationPresent(final Class<?> clazz) {
        return !clazz.isAnnotationPresent(Entity.class);
    }

    @Override
    public PrimaryKey primaryKey() {
        return this.primaryKey;
    }

    private PrimaryKey createPrimaryKey(Class<?> clazz) {
        final SimpleColumn simpleColumn = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isIdField)
                .findFirst()
                .map(this::createColumn)
                .orElseThrow(IllegalArgumentException::new);
        return new SimplePrimaryKey(simpleColumn);
    }

    private PrimaryKey createPrimaryKeyValue(Object object) {
        final SimpleColumn simpleColumn = Arrays.stream(object.getClass().getDeclaredFields())
                .filter(this::isIdField)
                .findFirst()
                .map(field -> createColumnValue(field, object))
                .orElseThrow(IllegalArgumentException::new);
        return new SimplePrimaryKey(simpleColumn);
    }

    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    @Override
    public Columns columns() {
        return this.columns;
    }

    private Columns createColumn(Class<?> clazz) {
        final List<SimpleColumn> simpleColumns = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isNotTransientField)
                .filter(this::isNotIdField)
                .map(this::createColumn)
                .collect(Collectors.toList());

        return new SimpleColumns(simpleColumns);
    }

    private Columns createColumnValues(Object object) {
        final List<SimpleColumn> simpleColumns = Arrays.stream(object.getClass().getDeclaredFields())
                .filter(this::isNotTransientField)
                .filter(this::isNotIdField)
                .map((Field field) -> createColumnValue(field, object))
                .collect(Collectors.toList());

        return new SimpleColumns(simpleColumns);
    }

    private boolean isNotIdField(Field field) {
        return !field.isAnnotationPresent(Id.class);
    }

    private boolean isNotTransientField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private SimpleColumn createColumn(final Field field) {
        return new SimpleColumn(getFieldName(field), isNullable(field), generateType(field), field.getType());
    }

    private SimpleColumn createColumnValue(final Field field, final Object object) {
        return new SimpleColumn(getFieldName(field), isNullable(field), generateType(field),
                field.getType(), createSimpleValue(field, object));
    }

    private SimpleValue createSimpleValue(Field field, Object object) {
        field.setAccessible(true);
        Object value;
        try {
            value = field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        if (field.getType().equals(String.class)) {
            return new SimpleValue(String.format("'%s'", value));
        }

        if (field.getType().equals(Long.class)) {
            return new SimpleValue( String.format("%dL", value));
        }

        return new SimpleValue(String.valueOf(value));
    }

    public String getFieldName(final Field field) {
        if (isNotBlankOf(field)) {
            return field.getAnnotation(jakarta.persistence.Column.class).name();
        }

        return field.getName();
    }

    private boolean isNotBlankOf(final Field field) {
        return field.isAnnotationPresent(jakarta.persistence.Column.class) && !field.getAnnotation(jakarta.persistence.Column.class).name().isBlank();
    }

    private boolean isNullable(final Field field) {
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getAnnotation(jakarta.persistence.Column.class).nullable();
        }

        return true;
    }

    public GenerationType generateType(final Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return field.getAnnotation(GeneratedValue.class).strategy();
        }

        return GenerationType.AUTO;
    }
}

