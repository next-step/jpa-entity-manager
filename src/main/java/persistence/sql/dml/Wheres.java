package persistence.sql.dml;

import java.util.List;

public class Wheres {

    private final List<Where> wheres;

    public Wheres(List<Where> wheres) {
        this.wheres = wheres;
        this.wheres.stream()
                .findFirst()
                .ifPresent(where -> where.changeLogicalOperator(LogicalOperator.NONE));
    }

    public List<Where> getWheres() {
        return this.wheres;
    }

}
