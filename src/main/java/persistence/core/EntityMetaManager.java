package persistence.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.Person;
import persistence.entity.Person1;
import persistence.entity.Person2;
import persistence.entity.metadata.EntityMetadata;
import persistence.entity.metadata.EntityMetadataBuilder;

import java.util.HashMap;
import java.util.Map;

public class EntityMetaManager {
    private static final Logger LOG = LoggerFactory.getLogger(EntityMetaManager.class);
    protected static final Map<Class<?>, EntityMetadata> ENTITIES = new HashMap<>();

    private static EntityMetaManager entityMetaManager;

    public static EntityMetaManager getInstance() {
        if (entityMetaManager == null) {
            entityMetaManager = new EntityMetaManager();
            entityMetaManager.loadEntities();   // TEST 편의상 인스턴스 최초 생성시 Load

        }
        return entityMetaManager;
    }

    public void loadEntities() {
        LOG.info("=============== Load EntityMetadata ===============");

        putEntityMetadata(Person.class);
        putEntityMetadata(Person1.class);
        putEntityMetadata(Person2.class);
    }

    protected EntityMetadata putEntityMetadata(Class<?> clazz) {
        EntityMetadata entityMetadata = EntityMetadataBuilder.build(clazz);
        ENTITIES.put(clazz, entityMetadata);

        return entityMetadata;
    }

    public EntityMetadata getEntityMetadata(Class<?> clazz) {

        return ENTITIES.putIfAbsent(clazz, putEntityMetadata(clazz));
    }

}
