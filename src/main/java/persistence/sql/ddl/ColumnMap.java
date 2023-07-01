package persistence.sql.ddl;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ColumnMap {
    private final LinkedHashMap<String, String> map;

    private ColumnMap(LinkedHashMap<String, String> map) {
        this.map = map;
    }

    public static ColumnMap of(Object object) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Transient.class) == null)
                .filter(field -> field.getAnnotation(Id.class) == null)
                .forEach(field -> map.put(columnName(field), getFieldValue(field, object)));

        return new ColumnMap(map);
    }

    public static ColumnMap empty() {
        return new ColumnMap(new LinkedHashMap<>());
    }

    public void add(String name, String value) {
        map.put(name, value);
    }

    public String names() {
        return String.join(",", map.keySet());
    }

    public String values() {
        return map.values().stream()
                .map(value -> {
                    if (value == null) {
                        return null;
                    }

                    return "'" + value + "'";
                })
                .collect(Collectors.joining(","));
    }

    private static String columnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        String name = field.getName();
        if (columnAnnotation != null && !columnAnnotation.name().equals("")) {
            name = columnAnnotation.name();
        }
        return name;
    }


    private static String getFieldValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            return String.valueOf(field.get(object));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Map.Entry<String, String>> entrySet() {
        return map.entrySet();
    }
}
