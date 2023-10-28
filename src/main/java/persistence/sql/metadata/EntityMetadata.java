package persistence.sql.metadata;

import jakarta.persistence.Entity;
import persistence.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMetadata {
    private final Table table;

    private final Columns columns;

    public EntityMetadata(Object entity) {
        if (!entity.getClass().isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Entity 클래스가 아닙니다.");
        }
        this.table = new Table(entity.getClass());
        this.columns = new Columns(convertEntityToColumnList(entity));
    }

    public EntityMetadata(Table table, Columns columns) {
        this.table = table;
        this.columns = columns;
    }

    private List<Column> convertEntityToColumnList(Object entity) {
        if(entity == null) {
            return null;
        }

        Field[] fields = entity.getClass().getDeclaredFields();

        return Arrays.stream(fields)
                .map(x -> {
                    x.setAccessible(true);

                    try {
                        return new Column(x, String.valueOf(x.get(entity)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public String getTableName() {
        return table.getName();
    }

    public String getColumnsToCreate(Dialect dialect) {
        return columns.buildColumnsWithConstraint(dialect);
    }

    public String buildColumnsClause() {
        return columns.buildColumnsClause();
    }

    public String buildValueClause() {
        return columns.buildValueClause();
    }

    public String buildSetClause() {
        return columns.buildSetClause();
    }

    public String buildWhereClause() {
        return columns.buildWhereClause();
    }

    public String buildWhereWithPKClause() {
        return columns.buildWhereWithPKClause();
    }
}
