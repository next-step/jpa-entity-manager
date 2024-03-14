package persistence.sql.dml;

import persistence.sql.dml.conditions.WhereCondition;
import persistence.sql.dml.conditions.WhereConditions;
import persistence.sql.dml.conditions.WhereRecord;
import persistence.sql.metadata.ColumnMetadata;
import persistence.sql.metadata.ColumnsMetadata;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;
import java.util.stream.Collectors;

public class WhereQueryBuilder {

    private static final String WHERE_CLAUSE_TEMPLATE = "WHERE %s";
    private final WhereConditions whereConditions;

    private WhereQueryBuilder(WhereConditions whereConditions) {
        this.whereConditions = whereConditions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WhereConditions whereConditions;

        private Builder() {
        }

        public Builder whereConditions(ColumnsMetadata columns, List<WhereRecord> whereRecords) {
            this.whereConditions = WhereConditions.of(whereRecords.stream()
                    .map(whereRecord -> WhereCondition.of(columns.getColumn(whereRecord.getName()), whereRecord.getOperator(), whereRecord.getValue()))
                    .collect(Collectors.toList()));
            return this;
        }

        public Builder whereConditions(EntityMetadata entityMetadata) {
            ColumnMetadata columnMetadata = entityMetadata.getColumnsMetadata().getColumn(entityMetadata.getPrimaryKey().getName());
            this.whereConditions = WhereConditions.of(List.of(WhereCondition.of(columnMetadata, "=", columnMetadata.getValue())));
            return this;
        }

        public WhereQueryBuilder build() {
            return new WhereQueryBuilder(whereConditions);
        }
    }

    public String generateWhereClausesQuery() {
        return String.format(WHERE_CLAUSE_TEMPLATE, whereConditions.generateWhereClausesQuery());
    }

}
