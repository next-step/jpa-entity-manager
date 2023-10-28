package persistence.sql.dml;

import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class InsertQueryBuilder {
    private static final String INSERT_COMMAND = "INSERT INTO %s (%s) VALUES %s;";

    public InsertQueryBuilder() {
    }

    public String buildQuery(EntityMetadata entityMetadata) {
        if(entityMetadata == null) {
            throw new IllegalArgumentException("등록하려는 객체가 NULL 값이 될 수 없습니다.");
        }

        return format(INSERT_COMMAND, entityMetadata.getTableName(), entityMetadata.buildColumnsClause(), "(" +  entityMetadata.buildValueClause() + ")");
    }
}
