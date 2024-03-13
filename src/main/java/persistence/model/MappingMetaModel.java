package persistence.model;

import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisterConcurrentMap;
import persistence.entity.persister.SingleTableEntityPersister;

public class MappingMetaModel {

    private final EntityPersisterConcurrentMap entityPersisterMap = new EntityPersisterConcurrentMap();

    public MappingMetaModel(final SingleTableEntityPersister... entityPersisters) {
        for (final SingleTableEntityPersister entityPersister : entityPersisters) {
            entityPersisterMap.put(entityPersister.getTargetEntityName(), entityPersister);
        }
    }

    public EntityPersister getEntityDescriptor(final Object entity) {
        final String entityName = entity.getClass().getName();
        return entityPersisterMap.get(entityName);
    }

}
