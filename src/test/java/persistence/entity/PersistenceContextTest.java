package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonWithTransientAnnotation;

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
        @DisplayName("제거하려는 엔티티가 저장되어 있지 않다면, 제거를 시도해도 기존 영속 객체들엔 아무 영향도 끼치지 않는다.")
        void testRemoveEntityNoAffect() {
            // given
            persistenceContext.addEntity(entity);
            Object beforeRemoveEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 1L);

            // when
            persistenceContext.removeEntity(new PersonWithTransientAnnotation("person2@test.com"));

            // then
            assertNotNull(beforeRemoveEntity);
        }

        @Test
        @DisplayName("스냅샷도 제거한다.")
        void testRemoveSnapshot() {
            // given
            persistenceContext.addEntity(entity);

            // when
            persistenceContext.removeEntity(new PersonWithTransientAnnotation("person2@test.com"));

            // then
            assertNotNull(persistenceContext.getSnapshot(entity));
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
        void failToUpdate() {
            assertThrows(IllegalArgumentException.class, () -> persistenceContext.updateEntity(entity));
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
}
