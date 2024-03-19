package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.dml.conditions.WhereRecord;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;
import java.util.Objects;


public class DeleteQueryBuilder {

    public static final String DELETE_TEMPLATE = "DELETE FROM %s";
    public static final String WHERE_DELIMITER = " ";
    private final Dialect dialect;
    private final EntityMetadata entity;
    private final WhereQueryBuilder whereQueryBuilder;

    private DeleteQueryBuilder(Dialect dialect, EntityMetadata entity, WhereQueryBuilder whereQueryBuilder) {
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

        public Builder entity(Object entity) {
            this.entity = EntityMetadata.of(entity.getClass(), entity);
            if (Objects.nonNull(this.entity.getPrimaryKey().getValue())) {
                this.whereQueryBuilder = WhereQueryBuilder.builder()
                        .whereConditions(this.entity)
                        .build();
            }
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

        public DeleteQueryBuilder build() {
            return new DeleteQueryBuilder(dialect, entity, whereQueryBuilder);
        }
    }

    public String generateQuery() {
        return String.format(DELETE_TEMPLATE, Objects.isNull(whereQueryBuilder) ? entity.getName() : String.join(WHERE_DELIMITER, entity.getName(), whereQueryBuilder.generateWhereClausesQuery()));
    }
}
