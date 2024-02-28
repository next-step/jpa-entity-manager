package persistence.sql.dml;

import org.junit.jupiter.api.Test;
import persistence.sql.domain.Query;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DeleteQueryBuilderTest {

    @Test
    void should_create_delete_query() {
        Person person = new Person(1l, "cs", 29, "katd216@gmail.com", 1);
        Query query = new DeleteQueryBuilder().delete(person);

        assertThat(query.getSql()).isEqualTo("delete users where id=1 and nick_name='cs' and old=29 and email='katd216@gmail.com';");
    }

}
