package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.Metadata;
import persistence.sql.domain.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateQueryBuilderTest {


    @Test
    @DisplayName("Person 객체로 Update Query 만들기")
    void updateQuery() {
        Person person = new Person(1L, "양승인", 33, "rhfpdk92@naver.com");
        Metadata metadata = new Metadata(Person.class);
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(metadata.getEntityTable(),metadata.getEntityColumns());
        String updateQuery = updateQueryBuilder.update(person, 1L);

        assertEquals(updateQuery, "update users set nick_name = '양승인', old = 33, email = 'rhfpdk92@naver.com' where id = 1");
    }


}
