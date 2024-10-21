package persistence.sql.definition;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import persistence.sql.Dialect;
import persistence.sql.Queryable;
import persistence.sql.ddl.query.AutoKeyGenerationStrategy;
import persistence.sql.ddl.query.IdentityKeyGenerationStrategy;
import persistence.sql.ddl.query.PrimaryKeyGenerationStrategy;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class TableId implements Queryable {

    private static final List<PrimaryKeyGenerationStrategy> pkGenerationStrategies = List.of(
            new AutoKeyGenerationStrategy(),
            new IdentityKeyGenerationStrategy()
    );

    private final GenerationType generationType;
    private final ColumnDefinition columnDefinition;
    private final PrimaryKeyGenerationStrategy strategy;

    public TableId(Field[] fields) {
        final Field pkField = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Primary key not found"));

        this.columnDefinition = new ColumnDefinition(pkField);
        this.generationType = determineGenerationType(pkField);
        this.strategy = findProperGenerationStrategy();
    }

    private static GenerationType determineGenerationType(Field field) {
        final boolean hasGeneratedValueAnnotation = field.isAnnotationPresent(GeneratedValue.class);

        if (hasGeneratedValueAnnotation) {
            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            return generatedValue.strategy();
        }

        return GenerationType.AUTO;
    }

    private PrimaryKeyGenerationStrategy findProperGenerationStrategy() {
        return pkGenerationStrategies.stream()
                .filter(pkStrategy -> pkStrategy.supports(this))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unsupported primary key generation strategy"));
    }

    public GenerationType generationType() {
        return generationType;
    }

    @Override
    public String getName() {
        return columnDefinition.getName();
    }

    @Override
    public String getDeclaredName() {
        return columnDefinition.declaredName();
    }

    @Override
    public void applyToCreateTableQuery(StringBuilder query, Dialect dialect) {
        final String type = dialect.translateType(columnDefinition);
        query.append(columnDefinition.getName()).append(" ").append(type);

        if (columnDefinition.isNotNullable()) {
            query.append(" NOT NULL");
        }

        query.append(" ").append(strategy.generatePrimaryKeySQL(this)).append(", ");
    }

    @Override
    public boolean hasValue(Object entity) {
        return columnDefinition.hasValue(entity);
    }

    @Override
    public String getValue(Object entity) {
        final Object value = columnDefinition.valueAsString(entity);

        if (value instanceof String) {
            return "'" + value + "'";
        }

        return value.toString();
    }

    @Override
    public void bindValue(Object entity, Object value) {
        columnDefinition.bindValue(entity, value);
    }

    public boolean shouldFetchId() {
        return strategy.shouldFetchId();
    }
}
