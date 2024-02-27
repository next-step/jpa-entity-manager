package persistence.sql.domain;

import org.junit.jupiter.api.Test;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateTest {

    @Test
    void should_return_update_set_clause() {
        Person person = new Person(1l, "cs", 29, "katd216@gmail.com", 0);
        DatabaseTable table = new DatabaseTable(person);

        String updateClause = new Update(table)
                .getUpdateClause();

        assertThat(updateClause).isEqualTo("nick_name='cs',old=29,email='katd216@gmail.com'");
    }
}
