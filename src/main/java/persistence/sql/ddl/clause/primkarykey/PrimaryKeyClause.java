package persistence.sql.ddl.clause.primkarykey;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import persistence.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Map;

public class PrimaryKeyClause {
    public static final String ID_AUTO_INCREMENT = "%s %s AUTO_INCREMENT PRIMARY KEY";
    public static final Map<GenerationType, String> sqlMap = Map.of(
            GenerationType.AUTO, ID_AUTO_INCREMENT,
            GenerationType.IDENTITY, ID_AUTO_INCREMENT,
            GenerationType.TABLE, "%s %s PRIMARY KEY, seq_value INT",
            GenerationType.UUID, "%s UUID PRIMARY KEY"
    );
    private final PrimaryKey value;
    private final GenerationType generationType;

    public PrimaryKeyClause(Class<?> clazz) {
        this.value = new PrimaryKey(clazz);
        this.generationType = getType(value.field());
    }

    private static GenerationType getType(Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return field.getAnnotation(GeneratedValue.class).strategy();
        }
        return GenerationType.IDENTITY;
    }

    public String getQuery() {
        if (generationType == GenerationType.UUID) {
            return String.format(sqlMap.get(generationType), value.name());
        }
        String query = String.format(sqlMap.get(generationType), value.name(), value.dataTypeName());
        if (query == null) {
            return ID_AUTO_INCREMENT;
        }
        return query;
    }

    public String name() {
        return this.value.name();
    }
}
