package persistence.sql.ddl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.exception.NoEntityException;
import persistence.fake.FakeDialect;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.NoHasEntity;
import persistence.testFixtures.Person;
import persistence.testFixtures.PkHasPerson;

@DisplayName("DropQueryBuilder 테스트")
class DropQueryBuilderTest {

    private Dialect dialect;

    @BeforeEach
    void setUp() {
        dialect = new FakeDialect();
    }

    @DisplayName("요구사항 4 - 정보를 바탕으로 drop 쿼리 만들어보기")
    @Nested
    class DropQuery {
        @Test
        @DisplayName("요구사항 4 - @Entity 어노테이션이 없는 경우 예외가 발생한다")
        void noEntity() {
            assertThatExceptionOfType(NoEntityException.class)
                    .isThrownBy(() -> QueryGenerator.of(NoHasEntity.class, dialect).drop());
        }

        @Test
        @DisplayName("@Table 어노테이션이 없는 경우")
        void noTable() {
            //given
            QueryGenerator ddl = QueryGenerator.of(PkHasPerson.class, dialect);

            //when
            String sql = ddl.drop();

            //then
            assertThat(sql).isEqualTo("DROP TABLE PkHasPerson");
        }

        @Test
        @DisplayName("@Table 어노테이션이 있는 경우 이름을 치환한다.")
        void hasTable() {
            //given
            QueryGenerator ddl = QueryGenerator.of(Person.class, dialect);

            //when
            String sql = ddl.drop();

            //then
            assertThat(sql).isEqualTo("DROP TABLE users");
        }
    }

}
