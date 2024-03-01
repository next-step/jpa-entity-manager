package persistence.sql.dml.builder;

import org.junit.jupiter.api.Test;
import persistence.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {
    @Test
    void updateQueryBuilder() {
        Person person = new Person(1L, "hoon25", 20, "hoon25@gmail.com");
        assertThat(new UpdateQueryBuilder().generateSQL(1L, person))
                .isEqualTo("UPDATE users SET id = '1', nick_name = 'hoon25', old = '20', email = 'hoon25@gmail.com' WHERE id = 1");
    }


}
