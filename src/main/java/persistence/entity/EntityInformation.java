package persistence.entity;

import persistence.sql.domain.DatabasePrimaryColumn;
import persistence.sql.domain.DatabaseTable;

public class EntityInformation {

    public boolean isNew(Object entity) {
        DatabaseTable table = new DatabaseTable(entity);
        DatabasePrimaryColumn primaryColumn = table.getPrimaryColumn();
        return !primaryColumn.hasColumnValue();
    }

}
