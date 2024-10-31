package persistence.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonWithTransientAnnotation;

import static org.junit.jupiter.api.Assertions.*;

public class EntityPrimaryKeyTest {
    @Nested
    @DisplayName("static build with object")
    class StaticBuildWithObject {
        @Test
        @DisplayName("엔티티 객체(Object 타입)에 PK 필드가 할당되어있지 않는 경우.")
        void testStaticBuildWithObjectWithoutPk() {
            PersonWithTransientAnnotation user = new PersonWithTransientAnnotation("test@test.com");
            EntityPrimaryKey pk = EntityPrimaryKey.build(user);

            assertAll(
                    () -> assertNull(pk.keyValue()),
                    () -> assertEquals("users", pk.entityTableName()),
                    () -> assertEquals("id", pk.keyName())
            );
        }

        @Test
        @DisplayName("엔티티 객체(Object 타입)에 PK 필드가 할당되어있는 경우.")
        void testStaticBuildWithObjectWithPk() {
            PersonWithTransientAnnotation user = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );
            EntityPrimaryKey pk = EntityPrimaryKey.build(user);

            assertAll(
                    () -> assertEquals(1L, pk.keyValue()),
                    () -> assertEquals("users", pk.entityTableName()),
                    () -> assertEquals("id", pk.keyName())
            );
        }
    }

    @Test
    @DisplayName("value가 없으면 isValid는 false이다.")
    void testIsValidAsFalse() {
        PersonWithTransientAnnotation user = new PersonWithTransientAnnotation("test@test.com");
        EntityPrimaryKey pk = EntityPrimaryKey.build(user);

        assertFalse(pk.isValid());
    }
}

