package persistence.sql.ddl.clause.column;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnClauses {
    private final List<ColumnClause> columnClauses;
    public ColumnClauses(List<Field> fields) {
        this.columnClauses = getColumnClauses(fields);
    }

    private static List<ColumnClause> getColumnClauses(List<Field> fields) {
        return fields.stream()
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(ColumnClause::new).collect(Collectors.toList());
    }

    public List<String> getQueries() {
        return this.columnClauses.stream().map(ColumnClause::getQuery).collect(Collectors.toList());
    }

    public List<String> getNames() {
        return this.columnClauses.stream().map(ColumnClause::name).collect(Collectors.toList());
    }
}
