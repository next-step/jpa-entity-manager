package persistence.entity;

import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityMetadataReader;
import persistence.inspector.EntityInfoExtractor;

public class EntityValue {

    private final EntityMetadataReader metadataReader;

    public EntityValue(EntityMetadataReader metadataReader) {
        this.metadataReader = metadataReader;
    }

    public <T> T getValue(Object entity, String columnName) {
        EntityColumn entityColumnByColumnName = metadataReader.getEntityColumnByColumnName(entity.getClass(), columnName);

        return EntityInfoExtractor.getFieldValue(entity, entityColumnByColumnName.getField());
    }
}
