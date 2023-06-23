package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;

import static org.assertj.core.api.Assertions.assertThat;

class InsertQueryBuilderTest {

    @DisplayName("엔티티를 저장하는 INSERT 쿼리를 생성한다.")
    @Test
    void build() {
        Person person = new Person("jack", 30, "jack@abc.com");
        String query = new InsertQueryBuilder(Person.class).build(person);
        assertThat(query).isEqualTo("INSERT INTO users ( nick_name, old, email ) VALUES ( 'jack', 30, 'jack@abc.com' )");
    }
}
