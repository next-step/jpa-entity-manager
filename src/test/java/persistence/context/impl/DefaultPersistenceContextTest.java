package persistence.context.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.context.PersistenceContext;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.fixture.TestPerson;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DefaultPersistenceContext 테스트")
class DefaultPersistenceContextTest extends TestEntityInitialize {
    private final PersistenceContext context = new DefaultPersistenceContext();


    @Test
    @DisplayName("get 함수는 저장된 엔티티를 반환한다.")
    void testGetWithEntity() {
        // given
        TestPerson entity = new TestPerson(1L, "catsbi", 33, "catsbi@naver.com", 123);
        context.add(entity.getId(), entity);

        // when
        TestPerson actual = context.get(TestPerson.class, entity.getId());

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(entity);
    }


    @Test
    @DisplayName("get 함수는 유효하지 않은 식별자를 전달하면 null을 반환한다.")
    void testGetWithInvalidId() {
        // when
        TestPerson actual = context.get(TestPerson.class, 1L);

        assertThat(actual).isNull();
    }
}
