package persistence.fakehibernate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.fakehibernate.FakePersistenceContext;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FakePersistenceContextTest {
    private FakePersistenceContext persistenceContext;
    private Long testId;
    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        persistenceContext = new FakePersistenceContext();
        testId = 1L;
        testEntity = new TestEntity(testId, "Test Name");
    }

    @Test
    void testAddAndGetEntity() {
        persistenceContext.add(testEntity, testId);
        Object retrievedEntity = persistenceContext.get(TestEntity.class, testId);
        assertEquals(testEntity, retrievedEntity, "The retrieved entity should match the added entity");
    }

    @Test
    void testEntityNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> persistenceContext.get(TestEntity.class, testId),
                "Should throw an exception if entity not found");
    }

    @Test
    void testUpdateEntity() {
        persistenceContext.add(testEntity, testId);
        // 업데이트할 새 엔티티 생성
        TestEntity updatedEntity = new TestEntity(testId, "Updated Name");
        persistenceContext.update(updatedEntity, testId);

        Object retrievedEntity = persistenceContext.get(TestEntity.class, testId);
        assertEquals(updatedEntity, retrievedEntity, "The entity should be updated with new values");
    }

    @Test
    void testRemoveEntity() {
        persistenceContext.add(testEntity, testId);
        persistenceContext.remove(TestEntity.class, testId);

        assertThrows(IllegalArgumentException.class,
                () -> persistenceContext.get(TestEntity.class, testId),
                "Entity not found");
    }

    @Test
    void testIsExist() {
        assertFalse(persistenceContext.isExist(TestEntity.class, testId), "Entity should not exist initially");

        persistenceContext.add(testEntity, testId);
        assertTrue(persistenceContext.isExist(TestEntity.class, testId), "Entity should exist after being added");
    }

    // TestEntity 클래스는 테스트에 사용되는 엔티티를 의미하며, equals 및 hashCode 메서드를 오버라이드 해야 합니다.
    static class TestEntity {
        private Long id;
        private String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // equals와 hashCode를 오버라이드하여 객체 비교가 정확하게 이루어지도록 함
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestEntity that = (TestEntity) o;
            return Objects.equals(id, that.id) && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }
}