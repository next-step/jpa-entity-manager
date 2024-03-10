package persistence.model;

import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisterConcurrentMap;

public class MappingMetaModel {

    private final EntityPersisterConcurrentMap entityPersisterMap = new EntityPersisterConcurrentMap();

    public EntityPersister getEntityDescriptor(String entityName) {
        return entityPersisterMap.get(entityName);
    }

}
