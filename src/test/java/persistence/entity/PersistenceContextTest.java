package persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextTest {

    @Entity(name = "TestEntity")
    private static class TestEntity {
        @Id
        private Long id;
        private String name;

        protected TestEntity() {
        }

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    void addEntity() {
        PersistenceContext persistenceContext = new PersistenceContext();
        EntityKey entityKey = new EntityKey(1L, TestEntity.class);
        Object entity = new TestEntity(1L, "Test");

        persistenceContext.addEntity(entityKey, entity);

        assertThat(persistenceContext.findEntity(entityKey)).isEqualTo(entity);
    }

    @Test
    void shouldDoNothingWhenAddTwice() {
        PersistenceContext persistenceContext = new PersistenceContext();
        EntityKey entityKey = new EntityKey(1L, TestEntity.class);
        Object entity = new TestEntity(1L, "Test");

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addEntity(entityKey, entity);

        assertThat(persistenceContext.findEntity(entityKey)).isEqualTo(entity);
    }

    @Test
    void removeEntity() {
        PersistenceContext persistenceContext = new PersistenceContext();
        EntityKey entityKey = new EntityKey(1L, TestEntity.class);
        Object entity = new TestEntity(1L, "Test");

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.removeEntity(entityKey);

        assertThat(persistenceContext.findEntity(entityKey)).isNull();
    }

}
