package persistence.sql.meta.simple;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleEntityMetaCreator  {

    public static SimpleTable tableOfClass(Class<?> clazz) {
        final String tableName = createTableName(clazz);
        final PrimaryKey primaryKey = createPrimaryKey(clazz);
        final Columns column = createColumn(clazz);
        return new SimpleTable(tableName, primaryKey, column);
    }

    public static SimpleTable tableOfInstance(Object object) {
        final String tableName = createTableName(object.getClass());
        final PrimaryKey primaryKeyValue = createPrimaryKeyValue(object);
        final Columns columnValues = createColumnValues(object);
        return new SimpleTable(tableName, primaryKeyValue, columnValues);
    }

    private static String createTableName(final Class<?> clazz) {
        if (isNotEntityAnnotationPresent(clazz)) {
            throw new IllegalStateException();
        }

        if (clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
            return clazz.getAnnotation(jakarta.persistence.Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    private static boolean isNotEntityAnnotationPresent(final Class<?> clazz) {
        return !clazz.isAnnotationPresent(Entity.class);
    }

    private static PrimaryKey createPrimaryKey(Class<?> clazz) {
        final SimpleColumn simpleColumn = Arrays.stream(clazz.getDeclaredFields())
                .filter(SimpleEntityMetaCreator::isIdField)
                .findFirst()
                .map(SimpleEntityMetaCreator::createColumn)
                .orElseThrow(IllegalArgumentException::new);
        return new SimplePrimaryKey(simpleColumn);
    }

    public static PrimaryKey createPrimaryKeyValue(Object object) {
        final SimpleColumn simpleColumn = Arrays.stream(object.getClass().getDeclaredFields())
                .filter(SimpleEntityMetaCreator::isIdField)
                .findFirst()
                .map(field -> createColumnValue(field, object))
                .orElseThrow(IllegalArgumentException::new);
        return new SimplePrimaryKey(simpleColumn);
    }

    public static PrimaryKey createPrimaryKeyValue(Class<?> clazz, Long id) {
        final SimpleColumn simpleColumn = Arrays.stream(clazz.getDeclaredFields())
                .filter(SimpleEntityMetaCreator::isIdField)
                .findFirst()
                .map(field -> createColumnValueById(field, id))
                .orElseThrow(IllegalArgumentException::new);
        return new SimplePrimaryKey(simpleColumn);
    }

    private static boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private static Columns createColumn(Class<?> clazz) {
        final List<SimpleColumn> simpleColumns = Arrays.stream(clazz.getDeclaredFields())
                .filter(SimpleEntityMetaCreator::isNotTransientField)
                .filter(SimpleEntityMetaCreator::isNotIdField)
                .map(SimpleEntityMetaCreator::createColumn)
                .collect(Collectors.toList());

        return new SimpleColumns(simpleColumns);
    }

    public static Columns createColumnValues(Object object) {
        final List<SimpleColumn> simpleColumns = Arrays.stream(object.getClass().getDeclaredFields())
                .filter(SimpleEntityMetaCreator::isNotTransientField)
                .filter(SimpleEntityMetaCreator::isNotIdField)
                .map((Field field) -> createColumnValue(field, object))
                .collect(Collectors.toList());

        return new SimpleColumns(simpleColumns);
    }

    private static boolean isNotIdField(Field field) {
        return !field.isAnnotationPresent(Id.class);
    }

    private static boolean isNotTransientField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private static SimpleColumn createColumn(final Field field) {
        return new SimpleColumn(getFieldName(field), isNullable(field), generateType(field), field.getType());
    }

    private static SimpleColumn createColumnValue(final Field field, final Object object) {
        return new SimpleColumn(getFieldName(field), isNullable(field), generateType(field),
                field.getType(), createSimpleValue(field, object));
    }
    private static SimpleColumn createColumnValueById(final Field field, final Long id) {
        Object key = null;
        try {
            field.setAccessible(true);
            key = field.get(new Person(id));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return new SimpleColumn(getFieldName(field), isNullable(field), generateType(field),
//                field.getType(), createSimpleValue(field, id));
                field.getType(), new SimpleValue(key));
    }


    private static SimpleValue createSimpleValue(Field field, Object object) {
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
//            return new SimpleValue( String.format("%dL", value));
            return new SimpleValue(value);
        }

        return new SimpleValue(String.valueOf(value));
    }

    public static String getFieldName(final Field field) {
        if (isNotBlankOf(field)) {
            return field.getAnnotation(jakarta.persistence.Column.class).name();
        }

        return field.getName();
    }

    private static boolean isNotBlankOf(final Field field) {
        return field.isAnnotationPresent(jakarta.persistence.Column.class) && !field.getAnnotation(jakarta.persistence.Column.class).name().isBlank();
    }

    private static boolean isNullable(final Field field) {
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getAnnotation(jakarta.persistence.Column.class).nullable();
        }

        return true;
    }

    public static GenerationType generateType(final Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return field.getAnnotation(GeneratedValue.class).strategy();
        }

        return GenerationType.AUTO;
    }
}
