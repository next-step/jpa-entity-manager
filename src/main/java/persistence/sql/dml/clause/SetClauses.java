package persistence.sql.dml.clause;

import persistence.sql.ddl.TableClause;
import persistence.sql.dml.value.ValueClauses;

import java.util.ArrayList;
import java.util.List;

public class SetClauses {
    public static final String EQUALS = "=";
    List<String> setClauses = List.of();
    public SetClauses(Class<?> entity) {
        var columnNames = new TableClause(entity).columnNames();
        var values = new ValueClauses(entity).values();
        for (int i = 0; i < columnNames.size(); i++) {
            setClauses.add(columnNames.get(i) + EQUALS + values.get(i));
        }
    }

    public List<String> query() {
        return setClauses;
    }
}
