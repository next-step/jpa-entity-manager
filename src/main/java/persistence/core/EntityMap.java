package persistence.core;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.metadata.EntityMetadata;

public class EntityMap {
    protected static final Map<Class, EntityMetadata> entities = new HashMap<Class, EntityMetadata>();

}
