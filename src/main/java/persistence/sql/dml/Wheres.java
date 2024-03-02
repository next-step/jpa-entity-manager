package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.QueryBuilderConst.ENTER;
import static persistence.sql.QueryBuilderConst.SPACE;

public class Wheres {

    private final List<Where> wheres;

    public Wheres(List<Where> wheres) {
        this.wheres = wheres;
        this.wheres.stream()
                .findFirst()
                .ifPresent(where -> where.changeLogicalOperator(LogicalOperator.NONE));
    }

    public String wheresClause() {
        return this.wheres.stream()
                .map(Where::getWhereClause)
                .collect(Collectors.joining(ENTER + SPACE));
    }

}
