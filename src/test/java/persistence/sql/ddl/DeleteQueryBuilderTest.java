package persistence.sql.ddl;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    @Test
    void delete() {
        DeleteQueryBuilder deleteQueryBuilder = new H2DeleteQueryBuilder();

        String actual = deleteQueryBuilder.delete("person", "id", "1");

        assertThat(actual).isEqualTo("delete from person where id = 1");
    }

}
