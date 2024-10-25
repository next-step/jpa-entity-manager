package persistence.sql.clause;

import jakarta.persistence.Id;
import persistence.sql.common.util.NameConverter;
import persistence.sql.dml.MetadataLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public record UpdateQueryClauses(List<Clause> clauses) {

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

        private static List<Field> extractDiffFields(Object entity, Object snapshotEntity, MetadataLoader<?> loader) {
            return loader.getFieldAllByPredicate(field -> {
                Object entityValue = Clause.extractValue(field, entity);
                Object snapshotValue = Clause.extractValue(field, snapshotEntity);

                if (entityValue == null && snapshotValue == null) {
                    return false;
                }

                if (entityValue == null || snapshotValue == null) {
                    return true;
                }

                return !entityValue.equals(snapshotValue);
            });
        }

        public Builder where(Object entity, MetadataLoader<?> loader) {
            WhereConditionalClause.builder()
                    .column(loader.getColumnName(loader.getPrimaryKeyField(), nameConverter))
                    .eq(Clause.toColumnValue(Clause.extractValue(loader.getPrimaryKeyField(), entity)));
            return this;
        }

        public Builder setColumnValues(Object entity, MetadataLoader<?> loader) {
            List<Field> fields = loader.getFieldAllByPredicate(field -> !field.isAnnotationPresent(Id.class));

            for (Field field : fields) {
                clauses.add(SetValueClause.newInstance(field, entity, loader.getColumnName(field, nameConverter)));
            }
            return this;
        }

        public Builder setColumnValues(Object entity, Object snapshotEntity, MetadataLoader<?> loader) {
            List<Field> diffFields = extractDiffFields(entity, snapshotEntity, loader);

            for (Field field : diffFields) {
                clauses.add(SetValueClause.newInstance(field, entity, loader.getColumnName(field, nameConverter)));
            }

            return this;
        }

        public UpdateQueryClauses build() {
            return new UpdateQueryClauses(clauses);
        }
    }
}
