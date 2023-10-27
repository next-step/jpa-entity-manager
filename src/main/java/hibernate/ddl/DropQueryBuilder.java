package hibernate.ddl;

import hibernate.entity.meta.EntityClass;

public class DropQueryBuilder {

    public static final DropQueryBuilder INSTANCE = new DropQueryBuilder();

    private static final String DROP_TABLE_QUERY = "drop table %s";

    private DropQueryBuilder() {
    }

    public String generateQuery(final EntityClass<?> entity) {
        return String.format(DROP_TABLE_QUERY, entity.tableName());
    }
}
