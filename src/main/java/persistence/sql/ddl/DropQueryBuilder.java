package persistence.sql.ddl;

import pojo.EntityMetaData;

public class DropQueryBuilder {

    private static final String DROP_TABLE_QUERY = "DROP TABLE %s IF EXISTS;";

    private final EntityMetaData entityMetaData;

    public DropQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public String dropTable() {
        return String.format(DROP_TABLE_QUERY, entityMetaData.getTableInfo().getName());
    }
}
