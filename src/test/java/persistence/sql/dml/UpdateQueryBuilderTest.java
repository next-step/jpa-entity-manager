package persistence.sql.dml;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.Person;

import static org.assertj.core.api.Assertions.*;

class UpdateQueryBuilderTest {

    @Test
    @DisplayName("update 쿼리를 만들 수 있다.")
    void buildUpdateQuery() {
        UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.getInstance();
        Person name = new Person(1L, "name", 10, "email.com", 1);
        String query = updateQueryBuilder.build(name);
        assertThat(query).isEqualTo("UPDATE users SET nick_name = 'name', old = 10, email = 'email.com' WHERE id = 1");
    }
}
