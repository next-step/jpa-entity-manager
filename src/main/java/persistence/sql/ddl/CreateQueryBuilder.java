package persistence.sql.ddl;

import static persistence.sql.ddl.common.StringConstants.COLUMN_DEFINITION_DELIMITER;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import persistence.sql.AbstractQueryBuilder;
import persistence.sql.EntityColumn;
import persistence.sql.EntityMetadata;
import persistence.sql.ddl.common.StringConstants;
import persistence.sql.ddl.constraints.strategy.ConstraintsStrategy;
import persistence.sql.ddl.constraints.strategy.DefaultConstraintsStrategy;
import persistence.sql.ddl.type.DataTypeMapping;
import persistence.sql.ddl.type.impl.DefaultDataTypeMapping;

public class CreateQueryBuilder extends AbstractQueryBuilder {
    private final DataTypeMapping dataTypeMapping;

    private final ConstraintsStrategy constraintsStrategy;

    public CreateQueryBuilder() {
        this(
            new DefaultDataTypeMapping(),
            new DefaultConstraintsStrategy()
        );
    }

    public CreateQueryBuilder(
        DataTypeMapping dataTypeMapping,
        ConstraintsStrategy constraintsStrategy
    ) {
        this.dataTypeMapping = dataTypeMapping;
        this.constraintsStrategy = constraintsStrategy;
    }

    public String getCreateTableQuery(final Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return String.format(
            "CREATE TABLE %s (%s)",
            entityMetadata.getTableName(),
            getColumnDefinitionsFrom(entityClass)
        );
    }

    public String getColumnDefinitionsFrom(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        return entityMetadata.getEntityColumns().stream()
            .map(this::getColumnDefinitionFrom)
            .collect(Collectors.joining(COLUMN_DEFINITION_DELIMITER));
    }

    public String getColumnDefinitionFrom(EntityColumn entityColumn) {
        return Stream.of(
                entityColumn.getColumnName(),
                dataTypeMapping.getDataTypeDefinitionFrom(entityColumn.getColumnField()),
                constraintsStrategy.getConstraintsFrom(entityColumn.getColumnField())
            )
            .filter(s -> !s.isBlank())
            .collect(Collectors.joining(StringConstants.SPACE));
    }

    public String getColumnDefinitionFrom(Field field) {
        return Stream.of(
                getColumnNameFrom(field),
                dataTypeMapping.getDataTypeDefinitionFrom(field),
                constraintsStrategy.getConstraintsFrom(field)
            )
            .filter(s -> !s.isBlank())
            .collect(Collectors.joining(StringConstants.SPACE));
    }
}
