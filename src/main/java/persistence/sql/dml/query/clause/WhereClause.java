package persistence.sql.dml.query.clause;

import persistence.sql.dml.conditional.Criteria;
import persistence.sql.entity.model.PrimaryDomainType;

import static persistence.sql.constant.SqlConstant.EMPTY;
import static persistence.sql.constant.SqlFormat.WHERE;

public class WhereClause {

    private final Criteria criteria;

    public WhereClause(final Criteria criteria) {
        this.criteria = criteria;
    }

    public String toSql() {
        String sql = criteria.toSql();

        return EMPTY.getValue().equals(sql) ?
                EMPTY.getValue() :
                String.format(WHERE.getFormat(), criteria.toSql());
    }

}
