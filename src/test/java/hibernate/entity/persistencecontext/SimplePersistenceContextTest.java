package hibernate.entity.persistencecontext;

import hibernate.EntityEntry;
import hibernate.Status;
import hibernate.entity.EntityEntryContext;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        Object actual = new SimplePersistenceContext(Map.of(givenKey, givenEntity), Map.of(), new EntityEntryContext()).getEntity(givenKey);

        assertThat(actual).isEqualTo(givenEntity);
    }

    @Test
    void entity를_저장한다() {
        // given
        EntityKey expectedEntityKey = new EntityKey(1L, TestEntity.class);
        TestEntity givenEntity = new TestEntity(1L);
        Map<EntityKey, Object> givenEntities = new ConcurrentHashMap<>();
        Map<EntityKey, EntitySnapshot> givenSnapshotEntities = new ConcurrentHashMap<>();
        SimplePersistenceContext persistenceContext = new SimplePersistenceContext(givenEntities, givenSnapshotEntities, new EntityEntryContext());

        // when
        persistenceContext.addEntity(1L, givenEntity);
        Object actual = givenEntities.get(expectedEntityKey);
        Object actualSnapshot = givenSnapshotEntities.get(expectedEntityKey);

        // then
        assertAll(
                () -> assertThat(actual).isEqualTo(givenEntity),
                () -> assertThat(actualSnapshot).isNotNull()
        );
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
        TestEntity givenEntity = new TestEntity(1L);
        Map<EntityKey, Object> givenEntities = new ConcurrentHashMap<>();
        givenEntities.put(givenEntityKey, givenEntity);
        Map<EntityKey, EntitySnapshot> givenSnapshotEntities = new ConcurrentHashMap<>();
        givenSnapshotEntities.put(givenEntityKey, new EntitySnapshot(givenEntity));
        Map<Object, EntityEntry> givenEntityEntries = new ConcurrentHashMap<>();
        givenEntityEntries.put(givenEntity, new EntityEntry(Status.MANAGED));
        SimplePersistenceContext persistenceContext = new SimplePersistenceContext(givenEntities, givenSnapshotEntities, new EntityEntryContext(givenEntityEntries));

        // when
        persistenceContext.removeEntity(givenEntity);

        // then
        assertThat(givenEntities).isEmpty();
    }

    @Test
    void 저장된_스냅샷_entity를_가져온다() {
        EntityKey givenKey = new EntityKey(1L, TestEntity.class);
        EntitySnapshot givenSnapshot = new EntitySnapshot(new TestEntity(1L));
        Object actual = new SimplePersistenceContext(Map.of(), Map.of(givenKey, givenSnapshot), new EntityEntryContext()).getDatabaseSnapshot(givenKey);

        assertThat(actual).isEqualTo(givenSnapshot);
    }

    @Entity
    static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public TestEntity() {
        }

        public TestEntity(Long id) {
            this.id = id;
        }
    }
}
