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

    private final Column id;

    public EntityMetadata(Object entity) {
        if (!entity.getClass().isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Entity 클래스가 아닙니다.");
        }
        this.table = new Table(entity.getClass());
        this.columns = Columns.convertEntityToColumnList(entity);
        this.id = columns.getId();
    }

    public EntityMetadata(Table table, Columns columns) {
        this.table = table;
        this.columns = columns;
        this.id = columns.getId();
    }

    public String getTableName() {
        return table.getName();
    }

    public Object getId() {
        return id.getValue();
    }

    public boolean isNewEntity() {
        return id.getValue() == null;
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
