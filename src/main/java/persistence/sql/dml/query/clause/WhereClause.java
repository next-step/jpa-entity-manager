package persistence.sql.dml.query.clause;

import persistence.sql.dml.conditional.Criteria;

public class WhereClause {

    private static final String FORMAT = "WHERE %s";
    private static final String EMPTY = "";

    private Criteria criteria;

    public WhereClause(final Criteria criteria) {
        this.criteria = criteria;
    }

    public String toSql() {
        String sql = criteria.toSql();

        return EMPTY.equals(sql) ?
                EMPTY :
                String.format(FORMAT, criteria.toSql());

    }

}
