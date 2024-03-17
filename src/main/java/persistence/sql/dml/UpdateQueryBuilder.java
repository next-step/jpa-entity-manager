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
    private final EntityMetadata entity;
    private final WhereQueryBuilder whereQueryBuilder;

    private UpdateQueryBuilder(Dialect dialect, EntityMetadata entity, WhereQueryBuilder whereQueryBuilder) {
        this.dialect = dialect;
        this.entity = entity;
        this.whereQueryBuilder = whereQueryBuilder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Dialect dialect;
        private EntityMetadata entity;
        private WhereQueryBuilder whereQueryBuilder;

        private Builder() {
        }

        public Builder dialect(Dialect dialect) {
            this.dialect = dialect;
            return this;
        }

        public Builder entity(Object object) {
            this.entity = EntityMetadata.of(object.getClass(), object);
            this.whereQueryBuilder = WhereQueryBuilder.builder()
                    .whereConditions(entity)
                    .build();
            return this;
        }

        public Builder where(List<WhereRecord> whereRecords) {
            if (Objects.isNull(entity)) {
                throw new IllegalStateException("Entity must be set before setting where clause");
            }

            this.whereQueryBuilder = WhereQueryBuilder.builder()
                    .whereConditions(entity.getColumnsMetadata(), whereRecords)
                    .build();
            return this;
        }

        public UpdateQueryBuilder build() {
            return new UpdateQueryBuilder(dialect, entity, whereQueryBuilder);
        }
    }

    private String setClause() {
        return entity.getColumns().stream()
                .filter(columnMetadata -> !entity.getPrimaryKey().getName().equals(columnMetadata.getName()))
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
        return String.format(UPDATE_TEMPLATE, entity.getName(), Objects.isNull(whereQueryBuilder) ? setClause() : String.join(WHERE_DELIMITER, setClause(), whereQueryBuilder.generateWhereClausesQuery()));
    }
}
