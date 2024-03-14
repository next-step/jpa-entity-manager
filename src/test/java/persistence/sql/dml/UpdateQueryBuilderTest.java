package persistence.sql.dml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.sql.dml.querybuilder.UpdateQueryBuilder;

class UpdateQueryBuilderTest {
    @Test
    @DisplayName("update 쿼리 빌더는 update 쿼리를 정상 리턴한다.")
    void getQueryTest() {
        Person person = new Person("김철수", 21, "chulsoo.kim@gmail.com", 11);
        String query = new UpdateQueryBuilder(Person.class).getQuery(person, 1L);

        Assertions.assertEquals("UPDATE users SET nick_name='김철수',old=21,email='chulsoo.kim@gmail.com' WHERE id = 1" , query);
    }
}
