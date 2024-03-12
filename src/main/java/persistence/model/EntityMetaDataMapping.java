package persistence.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityMetaDataMapping {

    private static final Map<String, EntityMetaData> entityMetaDataMap = new HashMap<>();

    // TODO 추후 4주차 미션에서 컴포넌트 스캔 단계 작업 시 컴포넌트 스캔으로 해결할 예정
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
