package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.entry.EntityEntry;
import persistence.entity.entry.EntityEntryStatus;
import persistence.fixture.PersonWithTransientAnnotation;
import persistence.model.EntityPrimaryKey;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceContextTest {
    PersistenceContext persistenceContext;
    PersonWithTransientAnnotation entity;

    @BeforeEach
    void setup() {
        persistenceContext = new PersistenceContextImpl();
        entity = new PersonWithTransientAnnotation(1L, "John Doe", 20, "test@test.com", 1);
    }

    @Nested
    @DisplayName("addEntry 테스트")
    class AddEntryTest {
        @Test
        @DisplayName("엔티티의 클래스, id 객체를 통해 특정 상태의 EntityEntry를 저장한다.")
        void testAddEntryWithClassAndId() throws NoSuchFieldException, IllegalAccessException {
            // when
            persistenceContext.addEntry(PersonWithTransientAnnotation.class, 1L, EntityEntryStatus.LOADING);

            // then
            assertEquals(EntityEntryStatus.LOADING, getEntityEntry().getStatus());
        }

        @Test
        @DisplayName("엔티티 객체를 통해 특정 상태의 EntityEntry를 저장한다.")
        void testAddEntryWithObject() throws NoSuchFieldException, IllegalAccessException {
            // when
            persistenceContext.addEntry(entity, EntityEntryStatus.LOADING);

            // then
            assertEquals(EntityEntryStatus.LOADING, getEntityEntry().getStatus());
        }

        @Test
        @DisplayName("Id값이 없는 엔티티 객체가 주어진다면 EntityEntry를 저장하지 않는다.")
        void testAddNoEntryWithObjectWithoutId() throws NoSuchFieldException, IllegalAccessException {
            // when
            persistenceContext.addEntry(
                    new PersonWithTransientAnnotation("new@test.com"),
                    EntityEntryStatus.LOADING
            );

            // then
            assertNull(getEntityEntry());
        }

        @Test
        @DisplayName("이미 존재하는 entityEntry가 있으면 에러를 내뱉는다.")
        void testUpdateEntryByAddEntry() {
            // given
            persistenceContext.addEntry(entity, EntityEntryStatus.SAVING);

            // when, then
            assertThrows(IllegalArgumentException.class, () -> {
                persistenceContext.addEntry(entity, EntityEntryStatus.MANAGED);
            });
        }
    }

    @Nested
    @DisplayName("updateEntry 테스트")
    class UpdateEntryTest {
        @Test
        @DisplayName("entry 상태를 업데이트한다.")
        void succeedToUpdateEntry() throws NoSuchFieldException, IllegalAccessException {
            // given
            persistenceContext.addEntity(entity);

            // when
            persistenceContext.updateEntry(entity, EntityEntryStatus.READ_ONLY);

            // then
            assertEquals(EntityEntryStatus.READ_ONLY, getEntityEntry().getStatus());
        }

        @Test
        @DisplayName("저장된 entry가 없다면 에러를 내뱉는다.")
        void failToUpdateEntryNotExists() {
            assertThrows(IllegalArgumentException.class, () -> {
                persistenceContext.updateEntry(entity, EntityEntryStatus.MANAGED);
            });
        }
    }

    @Nested
    @DisplayName("getEntity 테스트")
    class GetEntityTest {
        @Test
        @DisplayName("PK를 통해 영속 상태의 엔티티를 조회할 수 있다.")
        void testGetEntityByPK() {
            // given
            persistenceContext.addEntity(entity);

            // when
            PersonWithTransientAnnotation foundEntity = persistenceContext.getEntity(
                    PersonWithTransientAnnotation.class,
                    1L
            );

            // expect
            assertEquals(entity.getId(), foundEntity.getId());
        }

        @Test
        @DisplayName("PK에 해당하는 영속 상태의 엔티티가 없다면 null을 반환한다.")
        void testGetNullByPK() {
            // given
            persistenceContext.addEntity(entity);

            // when
            Object foundEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 2L);

            // expect
            assertNull(foundEntity);
        }
    }

    @Nested
    @DisplayName("addEntity 테스트")
    class AddEntityTest {
        @Test
        @DisplayName("엔티티를 영속 상태로 저장한다.")
        void testAddEntity() {
            Object beforeAddEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 1L);

            // given
            persistenceContext.addEntity(entity);

            // when
            PersonWithTransientAnnotation afterAddEntity = persistenceContext.getEntity(
                    PersonWithTransientAnnotation.class,
                    1L
            );

            // expect
            assertAll(
                    () -> assertNull(beforeAddEntity),
                    () -> assertNotNull(afterAddEntity),
                    () -> assertEquals(entity.getId(), afterAddEntity.getId())
            );
        }

        @Test
        @DisplayName("엔티티를 스냅샷으로 저장한다.")
        void testAddEntityAsSnapshot() {
            // given
            persistenceContext.addEntity(entity);

            // when
            EntitySnapshot snapshot = persistenceContext.getSnapshot(entity);

            // expect
            assertEquals(entity, snapshot.getOriginalEntity());
        }

        @Test
        @DisplayName("addEntity 후 엔티티의 Entry 상태는 MANAGED여야 한다.")
        void testEntryStatusAfterAddEntity() throws NoSuchFieldException, IllegalAccessException {
            // when
            persistenceContext.addEntity(entity);

            // expect
            assertEquals(EntityEntryStatus.MANAGED, getEntityEntry().getStatus());
        }
    }

    @Nested
    @DisplayName("removeEntity 테스트")
    class RemoveEntityTest {
        @Test
        @DisplayName("영속상태에 있는 엔티티를 제거한다.")
        void testRemoveEntity() {
            // given
            persistenceContext.addEntity(entity);
            Object beforeRemoveEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 1L);

            // when
            persistenceContext.removeEntity(entity);

            // then
            Object afterRemoveEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 1L);

            assertAll(
                    () -> assertNotNull(beforeRemoveEntity),
                    () -> assertNull(afterRemoveEntity)
            );
        }

        @Test
        @DisplayName("스냅샷을 제거한다.")
        void testRemoveSnapshot() {
            // given
            persistenceContext.addEntity(entity);

            // when
            persistenceContext.removeEntity(entity);

            // then
            assertNull(persistenceContext.getSnapshot(entity));
        }

        @Test
        @DisplayName("removeEntity 후 엔티티의 Entry 상태는 DELETED이어야 한다.")
        void testEntryStatusAfterRemoveEntity() throws NoSuchFieldException, IllegalAccessException {
            // given
            persistenceContext.addEntity(entity);

            // when
            persistenceContext.removeEntity(entity);

            // then
            assertEquals(EntityEntryStatus.DELETED, getEntityEntry().getStatus());
        }

        @Test
        @DisplayName("삭제 가능한 상태의 엔티티가 아니라면 에러를 내뱉는다.")
        void failToRemoveEntityForInvalidTransition() {
            // given
            persistenceContext.addEntity(entity);
            persistenceContext.updateEntry(entity, EntityEntryStatus.READ_ONLY);

            // when
            assertThrows(IllegalStateException.class, () -> persistenceContext.removeEntity(entity));
        }

        @Test
        @DisplayName("제거하려는 엔티티가 저장되어 있지 않다면, 에러를 반환한다.")
        void failToRemoveEntityForNotInContextYet() {
            // when, then
            assertThrows(IllegalArgumentException.class, () -> {
                persistenceContext.removeEntity(new PersonWithTransientAnnotation(
                        2L,
                        "John Doe",
                        20,
                        "test2@test.com",
                        2)
                );
            });
        }

        @Test
        @DisplayName("제거하려는 엔티티의 id가 null이라면, 에러를 반환한다.")
        void failToRemoveEntityForNullId() {
            // given
            entity = new PersonWithTransientAnnotation("John Doe", 20, "test@test.com", 1);
            persistenceContext.addEntity(entity);

            // when, then
            assertThrows(IllegalArgumentException.class, () -> persistenceContext.removeEntity(entity));
        }
    }

    @Nested
    @DisplayName("updateEntity 테스트")
    class UpdateEntityTest {
        @Test
        @DisplayName("기존 영속 객체가 값이 변경되었다면 스냅샷을 더티체크한다.")
        void succeedToUpdateEntity() {
            // given
            persistenceContext.addEntity(entity);

            // when
            entity.setAge(100);
            persistenceContext.updateEntity(entity);

            // then
            EntitySnapshot afterUpdateSnapshot = persistenceContext.getSnapshot(entity);

            assertTrue(afterUpdateSnapshot.isDirty());
        }

        @Test
        @DisplayName("기존 영속 객체가 값이 변경된 사항이 없다면 아무 일도 일어나지 않는다.")
        void succeedToUpdateNothing() {
            // given
            persistenceContext.addEntity(entity);

            // when
            persistenceContext.updateEntity(entity);

            // then
            EntitySnapshot afterUpdateSnapshot = persistenceContext.getSnapshot(entity);

            assertFalse(afterUpdateSnapshot.isDirty());
        }

        @Test
        @DisplayName("존재하지 않는 영속 객체가 주어지면 에러가 발생한다.")
        void failToUpdateForNotExists() {
            assertThrows(IllegalArgumentException.class, () -> persistenceContext.updateEntity(entity));
        }

        @Test
        @DisplayName("엔티티 엔트리가 업데이트 불가한 상태라면 에러가 발생한다.")
        void failToUpdateForReadOnlyStatus() {
            // given
            persistenceContext.addEntity(entity);
            persistenceContext.updateEntry(entity, EntityEntryStatus.READ_ONLY);

            // when, then
            assertThrows(IllegalStateException.class, () -> persistenceContext.updateEntity(entity));
        }
    }

    @Nested
    @DisplayName("isEntityExists 테스트")
    class IsEntityExistsTest {
        @Test
        @DisplayName("영속 객체가 있다면 true를 반환한다.")
        void testExistingEntity() {
            // given
            persistenceContext.addEntity(entity);

            // when
            boolean isExists = persistenceContext.isEntityExists(entity);

            // then
            assertTrue(isExists);
        }

        @Test
        @DisplayName("일치하는 영속 객체가 없다면 false를 반환한다.")
        void testNonExistingEntity() {
            // when
            boolean isExists = persistenceContext.isEntityExists(entity);

            // then
            assertFalse(isExists);
        }
    }

    private EntityEntry getEntityEntry() throws NoSuchFieldException, IllegalAccessException {
        Field entityEntriesField = persistenceContext.getClass().getDeclaredField("entityEntries");
        entityEntriesField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<EntityKey, EntityEntry> entityEntries = (Map<EntityKey, EntityEntry>)
                entityEntriesField.get(persistenceContext);

        EntityKey entityKey = new EntityKey(entity.getClass(), EntityPrimaryKey.build(entity));
        return entityEntries.get(entityKey);
    }
}
