package persistence.context.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.context.PersistenceContext;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.fixture.PersonV3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("DefaultPersistenceContext 테스트")
class DefaultPersistenceContextTest {
    private final PersistenceContext context = new DefaultPersistenceContext();


    @Test
    @DisplayName("get 함수는 저장된 엔티티를 반환한다.")
    void testGetWithEntity() {
        // given
        PersonV3 entity = new PersonV3(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.add(entity.getId(), entity);

        // when
        PersonV3 actual = context.get(PersonV3.class, entity.getId());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(entity);
    }


    @Test
    @DisplayName("get 함수는 유효하지 않은 식별자를 전달하면 null을 반환한다.")
    void testGetWithInvalidId() {
        // when
        PersonV3 actual = context.get(PersonV3.class, 1L);

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("merge 함수는 기존에 저장된 엔티티가 없을 경우 새로 저장한다.")
    void testMergeWithNewEntity() {
        // given
        PersonV3 entity = new PersonV3(1L, "catsbi", 33, "catsbi@naver.com", 123);

        // when
        context.merge(entity.getId(), entity);

        // then
        PersonV3 actual = context.get(PersonV3.class, entity.getId());
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).isEqualTo(entity)
        );
    }

    @Test
    @DisplayName("merge 함수는 기존에 저장된 엔티티가 있을 경우 엔티티를 덮어쓴다.")
    void testMergeWithExistedEntity() {
        // given
        PersonV3 entity = new PersonV3(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.add(entity.getId(), entity);

        // when
        PersonV3 newEntity = new PersonV3(1L, "catsbi2", 34, "catsbi2@naver.com", 123);
        context.merge(newEntity.getId(), newEntity);

        // then
        PersonV3 actual = context.get(PersonV3.class, entity.getId());
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(newEntity.getName()),
                () -> assertThat(actual.getAge()).isEqualTo(newEntity.getAge()),
                () -> assertThat(actual.getEmail()).isEqualTo(newEntity.getEmail())
        );
    }

    @Test
    @DisplayName("merge 함수는 기존에 저장된 엔티티가 존재하고 변경된 내용이 없을 경우 그대로 반환한다.")
    void testMergeWithNotChangedEntity() {
        // given
        PersonV3 entity = new PersonV3(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.add(entity.getId(), entity);

        // when
        context.merge(entity.getId(), entity);

        // then
        PersonV3 actual = context.get(PersonV3.class, entity.getId());

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).isEqualTo(entity)
        );
    }
}
