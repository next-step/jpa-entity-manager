package persistence.sql.dml;

import persistence.sql.common.instance.InstanceManager;
import persistence.sql.common.instance.Value;
import persistence.sql.common.meta.EntityMeta;

public class InsertQuery {
    private static final String DEFAULT_INSERT_COLUMN_QUERY = "INSERT INTO %s (%s)";
    private static final String DEFAULT_INSERT_VALUE_QUERY = "VALUES(%s)";
    private EntityMeta entityMeta;

    protected <T> InsertQuery(T t) {
        this.entityMeta = EntityMeta.of(t.getClass());
    }

    public static <T> String create(T t) {
        return new InsertQuery(t).combineQuery(t);
    }

    /**
     * 해당 Class를 분석하여 INSERT QUERY로 조합합니다.
     */
    private <T> String combineQuery(T t) {
        return String.join(" ", parseColumns(), parseValues(t));
    }

    private String parseColumns() {
        return String.format(DEFAULT_INSERT_COLUMN_QUERY, entityMeta.getTableName(), entityMeta.getColumnsWithComma());
    }

    private <T> String parseValues(T t) {
        return String.format(DEFAULT_INSERT_VALUE_QUERY, InstanceManager.getValuesWithComma(Value.of(t)));
    }
}
