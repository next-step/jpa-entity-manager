package persistence.sql.dml.clause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindOptionBuilder {
    private List<String> selectingColumns = new ArrayList<>();
    private final List<Clause> whereClauses = new ArrayList<>();

    public FindOptionBuilder selectColumns(String... selectingColumn) {
        selectingColumns = Arrays.stream(selectingColumn).toList();
        return this;
    }

    public FindOptionBuilder where(Clause equalClause) {
        whereClauses.add(new WhereClause(equalClause));
        return this;
    }

    public FindOptionBuilder where(Clause... equalClause) {
        whereClauses.add(new WhereClause(Arrays.stream(equalClause).toList()));
        return this;
    }

    public FindOption build() {
        return new FindOption(selectingColumns, whereClauses);
    }
}
