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
    class GetEntityTest {
        @Test
        @DisplayName("PK를 통해 영속 상태의 엔티티를 조회할 수 있다.")
        void testGetEntityByPK() {
            // given
            persistenceContext.addEntity(entity);

            // when
            Object foundEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 1L);

            // expect
            assertNotNull(foundEntity);
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
    class AddEntityTest {
        @Test
        @DisplayName("엔티티를 영속 상태로 저장한다.")
        void testAddEntity() {
            Object beforeAddEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 1L);

            // given
            persistenceContext.addEntity(entity);

            // when
            Object afterAddEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, 1L);

            // expect
            assertAll(
                    () -> assertNull(beforeAddEntity),
                    () -> assertNotNull(afterAddEntity)
            );
        }
    }

    @Nested
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
    }
}
