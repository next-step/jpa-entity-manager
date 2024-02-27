package persistence.sql.column;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;

public class IdColumn implements Column {

    private static final String PK_FORMAT = "%s %s %s";
    private static final String PRIMARY_KEY = "primary key";

    private final GeneralColumn generalColumn;
    private final IdGeneratedStrategy idGeneratedStrategy;

    public IdColumn(Field[] fields, Dialect dialect) {
        Field idField = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[INFO] No @Id annotation"));

        validateGeneratedValue(idField);
        this.generalColumn = new GeneralColumn(idField, dialect);
        this.idGeneratedStrategy = getIdGeneratedStrategy(dialect, idField);
    }

    public IdColumn(Object object, Dialect dialect) {
        Field[] fields = object.getClass().getDeclaredFields();
        Field idField = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[INFO] No @Id annotation"));

        validateGeneratedValue(idField);
        this.generalColumn = new GeneralColumn(object, idField, dialect);
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

    public IdGeneratedStrategy getIdGeneratedStrategy() {
        return idGeneratedStrategy;
    }

    public Long getValue(){
        return (Long) generalColumn.getValue();
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
