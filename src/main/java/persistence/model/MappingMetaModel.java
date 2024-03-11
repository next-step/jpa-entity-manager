package persistence.model;

import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisterConcurrentMap;
import persistence.entity.persister.SingleTableEntityPersister;

public class MappingMetaModel {

    private final EntityPersisterConcurrentMap entityPersisterMap = new EntityPersisterConcurrentMap();

    // TODO 추후 4주차 미션에서 컴포넌트 스캔 단계 작업 시 컴포넌트 스캔으로 해결할 예정
    public MappingMetaModel(final SingleTableEntityPersister... entityPersisters) {
        for (final SingleTableEntityPersister entityPersister : entityPersisters) {
            entityPersisterMap.put(entityPersister.getTargetEntityName(), entityPersister);
        }
    }

    public EntityPersister getEntityDescriptor(String entityName) {
        return entityPersisterMap.get(entityName);
    }

}
