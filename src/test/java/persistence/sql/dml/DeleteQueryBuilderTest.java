package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.h2.H2Dialect;
import persistence.exception.FieldEmptyException;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class DeleteQueryBuilderTest {
    private Dialect dialect;

    @BeforeEach
    void setUp() {
        dialect = new H2Dialect();
    }

    @Test
    @DisplayName("삭제하는 구문을 생성한다.")
    void delete() {
        //given
        DeleteQueryBuilder query = QueryGenerator.of(Person.class, dialect).delete();

        //when
        final String sql = query.build(1L);

        //then
        assertThat(sql).isEqualTo("DELETE FROM users WHERE id = 1");
    }

    @Test
    @DisplayName("id가 비어있으면 예외가 발생한다.")
    void deleteId() {
        //given
        DeleteQueryBuilder query = QueryGenerator.of(Person.class, dialect).delete();

        //when & then
        assertThatExceptionOfType(FieldEmptyException.class)
                .isThrownBy(() -> query.build(null));
    }

}
