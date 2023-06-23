package persistence.sql.ddl;

import domain.Person;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SelectQueryBuilderTest extends DatabaseTest {
    @Test
    void findAll() {
        SelectQueryBuilder selectQueryBuilder = new H2SelectQueryBuilder();
        insertDb();

        String actual = selectQueryBuilder.findAll("person");

        assertAll(
                () -> assertThat(actual).isEqualTo("select * from person"),
                () -> assertThat(query(actual)).hasSize(1)
        );
    }

    @Test
    void findById() {
        SelectQueryBuilder selectQueryBuilder = new H2SelectQueryBuilder();
        insertDb();

        String actual = selectQueryBuilder.findById("person", "id", "1");

        assertAll(
                () -> assertThat(actual).isEqualTo("select * from person where id=1"),
                () -> assertNotNull(queryForObject(actual))
        );
    }
}
