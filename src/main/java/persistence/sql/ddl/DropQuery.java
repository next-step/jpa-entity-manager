package persistence.sql.ddl;

import persistence.sql.common.meta.EntityMeta;

public class DropQuery {

    private static final String DEFAULT_DROP_QUERY = "DROP TABLE %s";

    private final EntityMeta entityMeta;

    public <T> DropQuery(T t) {
        entityMeta = EntityMeta.of(t.getClass());
    }

    public static <T> String drop(Class<T> tClass) {
        return new DropQuery(tClass).combineQuery();
    }

    private String combineQuery() {
        return String.format(DEFAULT_DROP_QUERY, entityMeta.getTableName());
    }
}
