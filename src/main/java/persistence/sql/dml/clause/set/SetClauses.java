package persistence.sql.dml.clause.set;

import java.util.ArrayList;
import java.util.List;

public class SetClauses {
    public static final String EQUALS = "=";
    private final List<String> setClauses = new ArrayList<>();
    public SetClauses(List<String> columnNames, List<String> values) {
        for (int i = 0; i < columnNames.size(); i++) {
            setClauses.add(columnNames.get(i) + EQUALS + values.get(i));
        }
    }

    public List<String> query() {
        return setClauses;
    }
}
