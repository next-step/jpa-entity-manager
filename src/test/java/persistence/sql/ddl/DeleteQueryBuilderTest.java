package persistence.sql.ddl;

import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest extends DatabaseTest {

    @Test
    void delete_query() {
        DeleteQueryBuilder deleteQueryBuilder = new H2DeleteQueryBuilder();

        String actual = deleteQueryBuilder.delete("person", "id", "1");

        execute(actual);
        assertThat(actual).isEqualTo("delete from person where id = 1");
    }
}
