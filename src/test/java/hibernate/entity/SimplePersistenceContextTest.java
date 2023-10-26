package hibernate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePersistenceContextTest {

    @Test
    void getEntity할_때_Entity가_없으면_null이_반환된다() {
        EntityKey givenKey = new EntityKey(1L, TestEntity.class);
        Object actual = new SimplePersistenceContext().getEntity(givenKey);
        assertThat(actual).isNull();
    }

    @Test
    void getEntity로_저장된_entity를_가져온다() {
        EntityKey givenKey = new EntityKey(1L, TestEntity.class);
        TestEntity givenEntity = new TestEntity(1L);
        Object actual = new SimplePersistenceContext(Map.of(givenKey, givenEntity)).getEntity(givenKey);

        assertThat(actual).isEqualTo(givenEntity);
    }

    @Entity
    static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id1;

        public TestEntity() {
        }

        public TestEntity(Long id1) {
            this.id1 = id1;
        }
    }
}
