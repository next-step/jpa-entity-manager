package persistence.defaulthibernate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityData;
import persistence.entity.EntityKey;

import static org.junit.jupiter.api.Assertions.*;

class DefaultPersistenceContextTest {
    private DefaultPersistenceContext persistenceContext;
    private Long testId;
    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        persistenceContext = new DefaultPersistenceContext();
        testId = 1L;
        testEntity = new TestEntity(testId, "Test Name");
    }

    @Test
    void testAddAndGetEntity() {
        EntityData entityData = new EntityData(testEntity);
        persistenceContext.add(entityData, new EntityKey(testId, TestEntity.class));
        EntityData retrievedEntity = persistenceContext.get(new EntityKey(testId, TestEntity.class));
        assertEquals(testEntity, retrievedEntity.entity(), "The retrieved entity should match the added entity");
    }

    @Test
    void testEntityNotFound() {
        EntityKey entityKey = new EntityKey(testId, TestEntity.class);
        assertThrows(IllegalArgumentException.class,
                () -> persistenceContext.get(entityKey),
                "Should throw an exception if entity not found");
    }

    @Test
    void testUpdateEntity() {
        persistenceContext.add(new EntityData(testEntity), new EntityKey(testId, TestEntity.class));
        // 업데이트할 새 엔티티 생성
        TestEntity updatedEntity = new TestEntity(testId, "Updated Name");
        EntityData updatedEntityData = new EntityData(updatedEntity);
        persistenceContext.update(updatedEntityData, new EntityKey(testId, TestEntity.class));

        EntityData retrievedEntity = persistenceContext.get(new EntityKey(testId, TestEntity.class));

        assertEquals(updatedEntityData, retrievedEntity);
    }

    @Test
    void testRemoveEntity() {
        persistenceContext.add(new EntityData(testEntity), new EntityKey(testId, TestEntity.class));
        persistenceContext.remove(new EntityKey(testId, TestEntity.class));

        assertThrows(IllegalArgumentException.class,
                () -> persistenceContext.get(new EntityKey(testId, TestEntity.class)),
                "Entity not found");
    }

    @Test
    void testIsExist() {
        assertFalse(persistenceContext.isExist(new EntityKey(testId, TestEntity.class)), "Entity should not exist initially");

        persistenceContext.add(new EntityData(testEntity), new EntityKey(testId, TestEntity.class));
        assertTrue(persistenceContext.isExist(new EntityKey(testId, TestEntity.class)), "Entity should exist after being added");
    }

    // TestEntity 클래스는 테스트에 사용되는 엔티티를 의미하며, equals 및 hashCode 메서드를 오버라이드 해야 합니다.
    class TestEntity {

        @Id
        @GeneratedValue
        private final Long id;
        private final String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}