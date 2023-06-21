package persistence.sql.ddl;

import domain.Person;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.h2.H2DropQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class DropQueryBuilderTest {

    @Test
    void dropTable() {
        DropQueryBuilder dropQueryBuilder = new H2DropQueryBuilder();

        String actual = dropQueryBuilder.createQueryBuild(Person.class);

        assertThat(actual).isEqualTo("drop table Person");
    }

}
