package persistence.sql.column;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Function;

public class IdColumn implements Column {

    private static final String PK_FORMAT = "%s %s %s";
    private static final String PRIMARY_KEY = "primary key";

    private final GeneralColumn generalColumn;
    private final IdGeneratedStrategy idGeneratedStrategy;

    public IdColumn(Field[] fields, Dialect dialect) {
        this(getIdField(fields),
                field -> new GeneralColumn(field, dialect),
                dialect);
    }

    public IdColumn(Object object, Dialect dialect) {
        this(getIdField(object.getClass().getDeclaredFields()),
                field -> new GeneralColumn(object, field, dialect),
                dialect);
    }

    private static Field getIdField(Field[] object) {
        return Arrays.stream(object)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[INFO] No @Id annotation"));
    }

    private IdColumn(Field idField, Function<Field, GeneralColumn> generalColumnCreator, Dialect dialect) {
        validateGeneratedValue(idField);
        this.generalColumn = generalColumnCreator.apply(idField);
        this.idGeneratedStrategy = getIdGeneratedStrategy(dialect, idField);
    }

    private IdGeneratedStrategy getIdGeneratedStrategy(Dialect dialect, Field idField) {
        GeneratedValue annotation = idField.getAnnotation(GeneratedValue.class);
        return dialect.getIdGeneratedStrategy(annotation.strategy());
    }

    private void validateGeneratedValue(Field field) {
        if (!field.isAnnotationPresent(GeneratedValue.class)) {
            throw new IllegalArgumentException("[INFO] No @GeneratedValue annotation");
        }
    }

    public boolean isNull() {
        return getValue() == null;
    }

    public IdGeneratedStrategy getIdGeneratedStrategy() {
        return idGeneratedStrategy;
    }

    public <T> T getValue(){
        return (T) generalColumn.getValue();
    }

    @Override
    public String getDefinition() {
        return String.format(PK_FORMAT, generalColumn.getDefinition(), idGeneratedStrategy.getValue(), PRIMARY_KEY);
    }

    @Override
    public String getName() {
        return generalColumn.getName();
    }

    @Override
    public String getFieldName() {
        return generalColumn.getFieldName();
    }
}
