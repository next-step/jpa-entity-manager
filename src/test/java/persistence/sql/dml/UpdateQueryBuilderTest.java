package persistence.sql.dml;

import org.junit.jupiter.api.Test;
import persistence.sql.domain.Query;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateQueryBuilderTest {

    @Test
    void should_create_update_query() {
        Person person = new Person(1l, "cs", 29, "katd216@gmail.com", 1);
        Query query = new UpdateQueryBuilder().update(person);

        assertThat(query.getSql()).isEqualTo("update users set nick_name='cs',old=29,email='katd216@gmail.com' where id=1;");
    }
}
