package persistence.sql.dml.query;

import org.junit.jupiter.api.Test;
import persistence.sql.Person;

import static org.assertj.core.api.Assertions.assertThat;

class SelectByIdQueryBuilderTest {
    @Test
    void testSelectById() {
        final String query = new SelectByIdQueryBuilder(Person.class, 1L).build();

        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1;");
    }

}
