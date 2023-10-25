package persistence.sql.dml;

import entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.entity.EntityData;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    @DisplayName("엔티티에 알맞는 update 쿼리를 생성한다.")
    @Test
    void updateQueryTest() {
        Person entity = new Person(1L, "test1", 10, "test1@gmail.com", 0);
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

        String actualQuery = updateQueryBuilder.generateQuery(new EntityData(entity.getClass()), entity);
        assertThat(actualQuery).isEqualTo("update users set nick_name = 'test1', old = 10, email = 'test1@gmail.com' where id = 1");
    }

}
