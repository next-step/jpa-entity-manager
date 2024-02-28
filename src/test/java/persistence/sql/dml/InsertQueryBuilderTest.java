package persistence.sql.dml;

import org.junit.jupiter.api.Test;
import persistence.sql.domain.Query;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class InsertQueryBuilderTest {

    @Test
    void should_create_insert_query() {
        Person person = new Person("cs", 29, "katd216@gmail.com", 1);

        Query query = new InsertQueryBuilder().insert(person);

        assertThat(query.getSql()).isEqualTo("insert into users(nick_name,old,email) values('cs',29,'katd216@gmail.com');");
    }
}
