package persistence.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.Person;
import persistence.entity.Person1;
import persistence.entity.Person2;
import persistence.entity.metadata.EntityMetadata;
import persistence.entity.metadata.EntityMetadataBuilder;

public class EntityContextManager extends EntityMap {
    private static final Logger LOG = LoggerFactory.getLogger(EntityContextManager.class);

    public static void loadEntities() {
        LOG.info("=============== Load EntityMetadata ===============");

        putEntityMetadata(Person.class);
        putEntityMetadata(Person1.class);
        putEntityMetadata(Person2.class);
    }

    protected static EntityMetadata putEntityMetadata(Class clazz) {
        EntityMetadata entityMetadata = EntityMetadataBuilder.build(clazz);
        entities.put(clazz, entityMetadata);

        return entityMetadata;
    }

    public static EntityMetadata getEntityMetadata(Class clazz) {
        return entities.putIfAbsent(clazz, EntityContextManager.putEntityMetadata(clazz));
    }




}
