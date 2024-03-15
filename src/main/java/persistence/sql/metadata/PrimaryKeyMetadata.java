package persistence.sql.metadata;

import java.lang.reflect.Field;

public class PrimaryKeyMetadata {

    private final String name;
    private final Object value;

    private PrimaryKeyMetadata(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public static PrimaryKeyMetadata of(ColumnMetadata column) {
        return new PrimaryKeyMetadata(column.getName(), column.getValue());
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object entity, Object value) {
        try {
            Field field = entity.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(entity, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
