package persistence.sql.dml.clause;

import persistence.sql.dialect.Dialect;

import java.util.List;
import java.util.stream.Collectors;

public class FindOption {
    private final List<String> selectingColumns;
    private final List<Clause> whereClauses;

    public FindOption(List<String> selectingColumns, List<Clause> whereClauses) {
        this.selectingColumns = selectingColumns;
        this.whereClauses = whereClauses;
    }

    public List<Clause> getWhere() {
        return whereClauses;
    }

    public List<String> getSelectingColumns() {
        return selectingColumns;
    }

    public String joinWhereClauses(Dialect dialect) {
        String whereQuery = "WHERE ";

        if (whereClauses.isEmpty()) {
            return "";
        }
        return whereQuery + whereClauses.stream()
                .map(where -> where.toSql(dialect))
                .collect(Collectors.joining(" OR "));
    }
}
