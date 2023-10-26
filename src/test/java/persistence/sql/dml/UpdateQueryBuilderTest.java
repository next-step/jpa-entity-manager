package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fake.FakeDialect;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class UpdateQueryBuilderTest {

    @Test
    @DisplayName("엔티티정보로 업데이트를 쿼리를 생성한다.")
    void update() {

        //given
        Person person =  new Person(1L, "이름",19,  "sad@gmail.com");

        //when
        String sql = QueryGenerator.of(Person.class, new FakeDialect())
                .update()
                .build(person);

        //then
        assertThat(sql).isEqualTo("UPDATE users SET nick_name='이름', old=19, email='sad@gmail.com' WHERE id = 1");
    }

}
