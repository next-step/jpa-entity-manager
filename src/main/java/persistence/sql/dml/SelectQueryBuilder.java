package persistence.sql.dml;

import jakarta.persistence.Transient;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SelectQueryBuilder extends DmlQueryBuilder {

    public SelectQueryBuilder(Class<?> entityClass) {
        super(entityClass);
    }

    public String buildFindAllQuery() {
        return new StringBuilder()
            .append(selectClause())
            .append(fromClause())
            .toString();
    }

    private String selectClause() {
        String columns = Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .map(this::getColumnName)
            .collect(Collectors.joining(", "));
        return String.format("SELECT %s ", columns);
    }

    private String fromClause() {
        return String.format("FROM %s", getTableName(entityClass));
    }

    public String buildFindByIdQuery(Long primaryKey) {
        return new StringBuilder(buildFindAllQuery())
            .append(whereClause(primaryKey))
            .toString();
    }
}
