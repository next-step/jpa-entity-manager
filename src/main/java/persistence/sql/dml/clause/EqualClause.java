package persistence.sql.dml.clause;

import persistence.sql.dialect.Dialect;

public class EqualClause implements Clause {
    private final String targetColumn;
    private final Object findingValue;

    public EqualClause(String targetColumn, Object findingValue) {
        this.targetColumn = targetColumn;
        this.findingValue = findingValue;
    }

    @Override
    public String toSql(Dialect dialect) {
        return dialect.getIdentifierQuoted(targetColumn) +
                " = " +
                dialect.getValueQuoted(findingValue);
    }
}
