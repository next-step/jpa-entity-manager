package persistence.sql.ddl.dialect;

import jakarta.persistence.GenerationType;
import persistence.sql.ddl.domain.Constraint;
import persistence.sql.ddl.domain.PrimaryKey;
import persistence.sql.ddl.domain.Type;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class H2Dialect implements Dialect {

    private static final String EMPTY_STRING = "";
    private static final String SPACE = " ";

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
    public String getTypeString(Type type, int length) {
        return String.join(EMPTY_STRING,
                types.get(type),
                getTypeLength(type, length)
        );
    }

    private String getTypeLength(Type type, int length) {
        if (type.getDefaultLength() == null) {
            return EMPTY_STRING;
        }
        if (length == 0) {
            return "(" + type.getDefaultLength() + ")";
        }
        return "(" + length + ")";
    }

    @Override
    public String getConstraintString(Constraint constraint) {
        return Stream.of(getIsNotNull(constraint),
                        getGenerationTypeString(constraint))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(SPACE));
    }

    private String getIsNotNull(Constraint constraint) {
        if (constraint.isNotNull()) {
            return "NOT NULL";
        }
        return EMPTY_STRING;
    }

    private String getGenerationTypeString(Constraint constraint) {
        if (constraint.isPrimaryKey()) {
            return Stream.of("PRIMARY KEY",
                            getGenerationType(constraint.getPrimaryKey()))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(SPACE));
        }
        return EMPTY_STRING;
    }

    private String getGenerationType(PrimaryKey primaryKey) {
        if (primaryKey.getGenerationType() == null) {
            return EMPTY_STRING;
        }
        return generationTypes.get(primaryKey.getGenerationType());
    }

}
