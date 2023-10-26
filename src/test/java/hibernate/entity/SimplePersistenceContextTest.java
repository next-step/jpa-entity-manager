package hibernate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void entity를_저장한다() {
        // given
        EntityKey expectedEntityKey = new EntityKey(1L, TestEntity.class);
        TestEntity givenEntity = new TestEntity();
        Map<EntityKey, Object> givenEntities = new ConcurrentHashMap<>();
        SimplePersistenceContext persistenceContext = new SimplePersistenceContext(givenEntities);

        // when
        persistenceContext.addEntity(1L, givenEntity);
        Object actual = givenEntities.get(expectedEntityKey);

        // then
        assertThat(actual).isEqualTo(givenEntity);
    }

    @Test
    void 영속되되지_않은_객체를_제거하려하는_경우_예외가_발생한다() {
        SimplePersistenceContext persistenceContext = new SimplePersistenceContext();
        assertThatThrownBy(() -> persistenceContext.removeEntity(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("영속화되어있지 않은 entity입니다.");
    }

    @Test
    void 영속화된_객체를_제거한다() {
        // given
        EntityKey givenEntityKey = new EntityKey(1L, TestEntity.class);
        TestEntity givenEntity = new TestEntity();
        Map<EntityKey, Object> givenEntities = new ConcurrentHashMap<>();
        givenEntities.put(givenEntityKey, givenEntity);
        SimplePersistenceContext persistenceContext = new SimplePersistenceContext(givenEntities);

        // when
        persistenceContext.removeEntity(givenEntity);

        // then
        assertThat(givenEntities).isEmpty();
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
