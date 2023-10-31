package persistence.entity.impl;

import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityObjectMappingMeta;

public class SnapShot {

    private final EntityObjectMappingMeta snapShotObjectMappingMeta;
    private final ColumnType columnType;

    public SnapShot(Object snapShotEntity, ColumnType columnType) {
        this.snapShotObjectMappingMeta = EntityObjectMappingMeta.of(snapShotEntity, columnType);
        this.columnType = columnType;
    }

    public boolean isSameWith(Object entity) {
        EntityObjectMappingMeta targetObjectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);
        return this.snapShotObjectMappingMeta.getDifferMetaEntryList(targetObjectMappingMeta).size() == 0;
    }
}
