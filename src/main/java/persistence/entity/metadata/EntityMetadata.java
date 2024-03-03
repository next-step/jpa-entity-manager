package persistence.entity.metadata;

import jdbc.PersonRowMapper;
import jdbc.RowMapper;

import java.util.List;
import java.util.stream.Collectors;

public class EntityMetadata {

    private EntityTable entityTable;
    private EntityColumns columns;

    public EntityTable getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(EntityTable entityTable) {
        this.entityTable = entityTable;
    }

    public EntityColumns getColumns() {
        return columns;
    }

    public void setColumns(EntityColumns columns) {
        this.columns = columns;
    }

}
