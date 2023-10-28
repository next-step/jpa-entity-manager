package persistence.entity.attribute;

import fixtures.TestEntityFixtures;

import java.util.HashMap;
import java.util.Map;

public class EntityAttributeHolder {
    //TODO Entity Scan 구현
    private static final Map<Class<?>, EntityAttribute> entityAttributeHolder = new HashMap<>();

    static {
        entityAttributeHolder.put(TestEntityFixtures.EntityWithIntegerId.class, EntityAttribute.of(TestEntityFixtures.EntityWithIntegerId.class));
        entityAttributeHolder.put(TestEntityFixtures.EntityWithOutEntityAnnotation.class, EntityAttribute.of(TestEntityFixtures.EntityWithOutEntityAnnotation.class));
        entityAttributeHolder.put(TestEntityFixtures.EntityWithMultiIdAnnotation.class, EntityAttribute.of(TestEntityFixtures.EntityWithMultiIdAnnotation.class));
        entityAttributeHolder.put(TestEntityFixtures.EntityWithStringId.class, EntityAttribute.of(TestEntityFixtures.EntityWithStringId.class));
        entityAttributeHolder.put(TestEntityFixtures.SampleOneWithValidAnnotation.class, EntityAttribute.of(TestEntityFixtures.SampleOneWithValidAnnotation.class));
        entityAttributeHolder.put(TestEntityFixtures.SampleTwoWithValidAnnotation.class, EntityAttribute.of(TestEntityFixtures.SampleTwoWithValidAnnotation.class));
    }

    public EntityAttribute findEntityAttribute(Class<?> clazz) {
        return entityAttributeHolder.get(clazz);
    }
}
