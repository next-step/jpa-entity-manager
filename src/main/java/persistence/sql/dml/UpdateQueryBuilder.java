package persistence.sql.dml;

import persistence.sql.domain.*;

public class UpdateQueryBuilder implements UpdateQueryBuild {

    private static final String UPDATE_TEMPLATE = "update %s set %s where %s;";

    @Override
    public Query update(Object entity) {
        DatabaseTable table = new DatabaseTable(entity);

        Condition condition = Condition.equal(table.getPrimaryColumn());
        String whereClause = new Where(table.getName())
                .and(condition)
                .getWhereClause();
        String updateClause = new Update(table).getUpdateClause();

        String sql = String.format(UPDATE_TEMPLATE, table.getName(), updateClause, whereClause);

        return new Query(sql, table);
    }
}
