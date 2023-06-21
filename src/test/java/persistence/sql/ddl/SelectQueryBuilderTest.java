package persistence.sql.ddl;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {
    @Test
    void findAll() {
        SelectQueryBuilder selectQueryBuilder = new H2SelectQueryBuilder();

        String actual = selectQueryBuilder.findAll("person");

        assertThat(actual).isEqualTo("select * from person");
    }

    @Test
    void findById() {
        SelectQueryBuilder selectQueryBuilder = new H2SelectQueryBuilder();

        String actual = selectQueryBuilder.findById("person", "id", "1");

        assertThat(actual).isEqualTo("select * from person where id=1");
    }
}
