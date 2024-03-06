package persistence.sql.ddl.dialect;

import jakarta.persistence.GenerationType;
import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Type;

import java.util.EnumMap;
import java.util.Map;

public class H2Dialect implements Dialect {

    private static final String EMPTY_STRING = "";

    private static final String PRIMARY_KEY_ATTRIBUTE = "PRIMARY KEY";
    private static final String NOT_NULL_ATTRIBUTE = "NOT NULL";

    private final Map<Type, String> types = new EnumMap<>(Type.class);
    private final Map<GenerationType, String> generationTypes = new EnumMap<>(GenerationType.class);

    public H2Dialect() {
        registerTypes();
        registerGenerationTypes();
    }

    private void registerTypes() {
        types.put(Type.TINYINT, "TINYINT");
        types.put(Type.SMALLINT, "SMALLINT");
        types.put(Type.INTEGER, "INTEGER");
        types.put(Type.VARCHAR, "VARCHAR");
        types.put(Type.BIGINT, "BIGINT");
    }

    private void registerGenerationTypes() {
        generationTypes.put(GenerationType.AUTO, "AUTO_INCREMENT");
        generationTypes.put(GenerationType.IDENTITY, "AUTO_INCREMENT");
    }

    @Override
    public String getTypeString(Type type) {
        return types.get(type);
    }

    @Override
    public String getPrimaryKeyString(Column column) {
        if (column.isPrimaryKey()) {
            return PRIMARY_KEY_ATTRIBUTE;
        }
        return EMPTY_STRING;
    }

    @Override
    public String getConstraintString(Column column) {
        if (column.isNotNull()) {
            return NOT_NULL_ATTRIBUTE;
        }
        return EMPTY_STRING;
    }

    @Override
    public String getGenerationTypeString(Column column) {
        if (column.isPrimaryKey() && column.getGenerationType() != null) {
            return generationTypes.get(column.getGenerationType());
        }
        return EMPTY_STRING;
    }

}
