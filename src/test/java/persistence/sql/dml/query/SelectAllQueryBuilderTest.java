package persistence.sql.dml.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.Person;

import static org.assertj.core.api.Assertions.assertThat;

class SelectAllQueryBuilderTest {
    @Test
    @DisplayName("Should build select all query")
    void shouldBuildSelectAllQuery() {
        String query = new SelectAllQueryBuilder().build(Person.class);

        assertThat(query).isEqualTo("SELECT * FROM users;");
    }
}
