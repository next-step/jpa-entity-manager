package persistence.sql.clause;

import persistence.sql.common.util.NameConverter;
import persistence.sql.dml.MetadataLoader;

import java.util.ArrayList;
import java.util.List;

public record DeleteQueryClauses(List<Clause> clauses) {

    public static Builder builder(NameConverter nameConverter) {
        return new Builder(nameConverter);
    }

    public Clause[] clauseArrays() {
        return clauses.toArray(Clause[]::new);
    }

    public static class Builder {
        private final NameConverter nameConverter;
        private final List<Clause> clauses = new ArrayList<>();

        public Builder(NameConverter nameConverter) {
            this.nameConverter = nameConverter;
        }

        public Builder where(Object entity, MetadataLoader<?> loader) {
            WhereConditionalClause.builder()
                    .column(loader.getColumnName(loader.getPrimaryKeyField(), nameConverter))
                    .eq(Clause.toColumnValue(Clause.extractValue(loader.getPrimaryKeyField(), entity)));
            return this;
        }

        public DeleteQueryClauses build() {
            return new DeleteQueryClauses(clauses);
        }
    }
}
