package persistence.context.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.context.PersistenceContext;
import persistence.sql.fixture.PersonV3;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("getAll 함수는 저장된 모든 엔티티를 반환한다.")
    void testGetAll() {
        // given
        List<PersonV3> expected = List.of(
                new PersonV3(1L, "catsbi", 12, "catsbi@naver.com", 123),
                new PersonV3(2L, "crong", 34, "crong@naver.com", 123),
                new PersonV3(3L, "pobi", 56, "pobi@naver.com", 123)
        );

        expected.forEach(person -> context.add(person.getId(), person));

        // when
        Collection<PersonV3> persons = context.getAll(PersonV3.class);

        // then
        assertThat(persons).isNotNull();
        assertThat(persons).hasSize(3);
        assertThat(persons).containsAll(expected);
    }

    @Test
    @DisplayName("getAll 함수는 저장된 엔티티가 없으면 빈 컬렉션을 반환한다.")
    void testGetAllWithEmpty() {
        // when
        Collection<PersonV3> persons = context.getAll(PersonV3.class);

        // then
        assertThat(persons).isNotNull();
        assertThat(persons).isEmpty();
    }
}
