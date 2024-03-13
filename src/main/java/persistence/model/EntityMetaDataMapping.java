package persistence.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityMetaDataMapping {

    private static final Map<String, EntityMetaData> entityMetaDataMap = new HashMap<>();

    public static void putMetaData(final Class<?> entityClass) {
        final EntityMetaData metaData = new EntityMetaData(entityClass);
        entityMetaDataMap.put(metaData.getEntityName(), metaData);
    }

    public static EntityMetaData getMetaData(final String entityName) {
        final EntityMetaData entityMetaData = entityMetaDataMap.get(entityName);
        if (Objects.isNull(entityMetaData)) {
            throw new MetaDataModelMappingException("entity meta data is not initialized : " + entityName);
        }

        return entityMetaData;
    }

}
