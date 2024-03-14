package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.dml.conditions.WhereRecord;
import persistence.sql.metadata.ColumnMetadata;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    public static final String UPDATE_TEMPLATE = "UPDATE %s SET %s";
    public static final String DELIMITER = ", ";
    public static final String WHERE_DELIMITER = " ";
    private final Dialect dialect;
    private final EntityMetadata entityMetadata;
    private final WhereQueryBuilder whereQueryBuilder;

    private UpdateQueryBuilder(Dialect dialect, EntityMetadata entityMetadata, WhereQueryBuilder whereQueryBuilder) {
        this.dialect = dialect;
        this.entityMetadata = entityMetadata;
        this.whereQueryBuilder = whereQueryBuilder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Dialect dialect;
        private EntityMetadata entityMetadata;
        private WhereQueryBuilder whereQueryBuilder;

        private Builder() {
        }

        public Builder dialect(Dialect dialect) {
            this.dialect = dialect;
            return this;
        }

        public Builder entity(Object object) {
            this.entityMetadata = EntityMetadata.of(object.getClass(), object);
            this.whereQueryBuilder = WhereQueryBuilder.builder()
                    .whereConditions(entityMetadata)
                    .build();
            return this;
        }

        public Builder where(List<WhereRecord> whereRecords) {
            if (Objects.isNull(entityMetadata)) {
                throw new IllegalStateException("Entity must be set before setting where clause");
            }

            this.whereQueryBuilder = WhereQueryBuilder.builder()
                    .whereConditions(entityMetadata.getColumnsMetadata(), whereRecords)
                    .build();
            return this;
        }

        public UpdateQueryBuilder build() {
            return new UpdateQueryBuilder(dialect, entityMetadata, whereQueryBuilder);
        }
    }

    private String setClause() {
        return entityMetadata.getColumns().stream()
                .filter(columnMetadata -> !entityMetadata.getPrimaryKey().getName().equals(columnMetadata.getName()))
                .filter(ColumnMetadata::isNotNull)
                .map(column -> column.getName() + " = " + generateColumnValue(column.getValue()))
                .collect(Collectors.joining(DELIMITER));
    }

    private String generateColumnValue(Object object) {
        if (object instanceof String) {
            return String.format("'%s'", object);
        } else {
            return String.valueOf(object);
        }
    }

    public String generateQuery() {
        return String.format(UPDATE_TEMPLATE, entityMetadata.getName(), Objects.isNull(whereQueryBuilder) ? setClause() : String.join(WHERE_DELIMITER, setClause(), whereQueryBuilder.generateWhereClausesQuery()));
    }
}
