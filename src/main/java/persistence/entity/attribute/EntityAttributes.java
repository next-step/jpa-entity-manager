package persistence.entity.attribute;

import fixtures.EntityFixtures;

import java.util.HashMap;
import java.util.Map;

public class EntityAttributes {
    //TODO Entity Scan 구현
    private static final Map<Class<?>, EntityAttribute> entityAttributeCenter = new HashMap<>();

    static {
        entityAttributeCenter.put(EntityFixtures.EntityWithIntegerId.class, EntityAttribute.of(EntityFixtures.EntityWithIntegerId.class));
        entityAttributeCenter.put(EntityFixtures.EntityWithStringId.class, EntityAttribute.of(EntityFixtures.EntityWithStringId.class));
        entityAttributeCenter.put(EntityFixtures.SampleOneWithValidAnnotation.class, EntityAttribute.of(EntityFixtures.SampleOneWithValidAnnotation.class));
        entityAttributeCenter.put(EntityFixtures.SampleTwoWithValidAnnotation.class, EntityAttribute.of(EntityFixtures.SampleTwoWithValidAnnotation.class));
    }

    public EntityAttribute findEntityAttribute(Class<?> clazz) {
        return entityAttributeCenter.get(clazz);
    }
}
