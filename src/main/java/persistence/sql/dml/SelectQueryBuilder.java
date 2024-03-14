package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.dml.conditions.WhereRecord;
import persistence.sql.metadata.ColumnMetadata;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SelectQueryBuilder {

    public static final String SELECT_TEMPLATE = "SELECT %s FROM %s";
    public static final String DELIMITER = ", ";
    public static final String WHERE_DELIMITER = " ";
    private final Dialect dialect;
    private final EntityMetadata entity;
    private final WhereQueryBuilder whereQueryBuilder;

    private SelectQueryBuilder(Dialect dialect, EntityMetadata entity, WhereQueryBuilder whereQueryBuilder) {
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

        public Builder entity(Class<?> clazz) {
            this.entity = EntityMetadata.from(clazz);
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

        public SelectQueryBuilder build() {
            return new SelectQueryBuilder(dialect, entity, whereQueryBuilder);
        }
    }

    private String columnsClause(List<ColumnMetadata> columns) {
        return columns.stream()
                .map(ColumnMetadata::getName)
                .collect(Collectors.joining(DELIMITER));
    }

    public String generateQuery() {
        return String.format(SELECT_TEMPLATE, columnsClause(entity.getColumns()),
                Objects.isNull(whereQueryBuilder) ? entity.getName() : String.join(WHERE_DELIMITER, entity.getName(), whereQueryBuilder.generateWhereClausesQuery()));
    }
}
