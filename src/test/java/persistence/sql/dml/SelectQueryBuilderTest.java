package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;


class SelectQueryBuilderTest {
    private Dialect dialect;

    @BeforeEach
    void setUp() {
        dialect = new FakeDialect();
    }

    @Test
    @DisplayName("전체를 조회하는 구문을 생성한다.")
    void selectAll() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Person.class, dialect).select();

        //when
        String sql = select.findAllQuery();

        //then
        assertThat(sql).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @Test
    @DisplayName("아이디를 기준으로 조회하는 구문을 생성한다.")
    void findById() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Person.class, dialect).select();

        //when
        String sql = select.findByIdQuery(1L);

        //then
        assertThat(sql).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }

    @Test
    @DisplayName("아이디가 없으면 예외가 발생한다.")
    void findByIdException() {
        //given
        SelectQueryBuilder select = QueryGenerator.of(Person.class, dialect).select();

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> select.findByIdQuery(null));
    }

}
