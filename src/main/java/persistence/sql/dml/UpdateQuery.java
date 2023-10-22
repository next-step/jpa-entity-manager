package persistence.sql.dml;

import persistence.sql.common.instance.InstanceManager;
import persistence.sql.common.meta.EntityMeta;

public class UpdateQuery {
    private static final String DEFAULT_UPDATE_COLUMN_QUERY = "UPDATE %s SET %s";
    private EntityMeta entityMeta;
    private Object arg;

    protected <T> UpdateQuery(T t, Object arg) {
        this.entityMeta = EntityMeta.of(t.getClass());
        this.arg = arg;
    }

    public static <T> String create(T t, Object args) {
        return new UpdateQuery(t, args).combine(t);
    }

    private <T> String combine(T t) {
        return String.join(" ", getTableQuery(t), getCondition());
    }

    private <T> String getTableQuery(T t) {
        return String.format(DEFAULT_UPDATE_COLUMN_QUERY, entityMeta.getTableName(), setChangeField(t));
    }

    private <T> String setChangeField(T t) {
        return InstanceManager.getFieldNameAndValue(t);
    }

    private String getCondition() {
        String condition = ConditionBuilder.getCondition(entityMeta.getIdName(), arg);
        return condition.replace(" id ", " " + setConditionField("id") + " ");
    }

    private String setConditionField(String word) {
        if (word.equals("id")) {
            word = entityMeta.getIdName();
        }
        return word;
    }
}
