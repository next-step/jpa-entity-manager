package persistence.sql.ddl.clause.table;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import persistence.sql.ddl.clause.column.ColumnClauses;
import persistence.sql.ddl.clause.primkarykey.PrimaryKeyClause;
import persistence.sql.exception.InvalidEntityException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TableClause {
    private final String name;
    private final PrimaryKeyClause primaryKeyClause;
    private final ColumnClauses columnClauses;
    private final Object instanceOfTable;

    public TableClause(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new InvalidEntityException();
        }
        this.name = getTableName(clazz);
        this.primaryKeyClause = new PrimaryKeyClause(clazz);
        this.columnClauses = extractColumnsFrom(clazz);
        this.instanceOfTable = getInstanceOfTable(clazz);
    }

    private Object getInstanceOfTable(Class<?> clazz) {
        try {
            return Arrays.stream(clazz.getDeclaredConstructors())
                    .filter(x -> x.getParameterCount() == 0)
                    .findFirst().get().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e){
            throw new RuntimeException("새로운 인스턴스 생성에 실패하였습니다.");
        }
    }

    private String getTableName(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
            return clazz.getSimpleName();
        }
        if (clazz.getAnnotation(jakarta.persistence.Table.class).name().isEmpty()) {
            return clazz.getSimpleName();
        }
        return clazz.getAnnotation(jakarta.persistence.Table.class).name();
    }

    private static ColumnClauses extractColumnsFrom(Class<?> clazz) {
        return new ColumnClauses(getColumnsFrom(clazz));
    }

    private static List<Field> getColumnsFrom(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(x -> !x.isAnnotationPresent(Id.class))
                .collect(Collectors.toList());
    }
    public String name() {
        return name;
    }

    public String createIdQuery() {
        return primaryKeyClause.getQuery();
    }

    public String primaryKeyName() {
        return primaryKeyClause.name();
    }

    public List<String> columnQueries() {
        return columnClauses.getQueries();
    }

    public List<String> columnNames() {
        return columnClauses.getNames();
    }

    public Object newInstance() {
        return this.instanceOfTable;
    }
}
