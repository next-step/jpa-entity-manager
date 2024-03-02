package persistence.sql.ddl.query.builder;

import persistence.sql.entity.EntityMappingTable;

import static persistence.sql.constant.SqlFormat.DROP;

public class DropQueryBuilder {

    private final EntityMappingTable entityMappingTable;

    public DropQueryBuilder(final EntityMappingTable entityMappingTable) {
        this.entityMappingTable = entityMappingTable;
    }

    public String toSql() {
        return String.format(DROP.getFormat(), entityMappingTable.getTableName().getName());
    }

}
