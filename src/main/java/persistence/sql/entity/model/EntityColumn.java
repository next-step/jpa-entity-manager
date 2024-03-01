package persistence.sql.entity.model;

public class EntityColumn {
    private static final String FORMAT = "'%s'";

    private final String name;
    private final Class<?> classType;
    private final Object value;

    public EntityColumn(final String name,
                        final Class<?> classType,
                        final Object value) {
        this.name = name;
        this.classType = classType;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public Object getValue() {
        return value;
    }

    public String getStringValue() {
        if (classType == String.class) {
            return String.format(FORMAT, value);
        }

        if (value == null) {
            return null;
        }

        return value.toString();
    }
}
