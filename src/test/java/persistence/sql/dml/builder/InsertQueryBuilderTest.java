package persistence.sql.dml.builder;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DummyPerson;

import static org.assertj.core.api.Assertions.assertThat;

class InsertQueryBuilderTest {

    private InsertQueryBuilder queryBuilder;
    private Person person;

    @BeforeEach
    void setUp() {
        person = DummyPerson.ofNullId();
        queryBuilder = new InsertQueryBuilder(person);
    }

    @Test
    void insertQueryTest() {
        final var expected = "INSERT INTO users (id, nick_name, old, email) VALUES (null, 'name', 10, 'a@a.com');";

        final var actual = queryBuilder.build();

        assertThat(actual).isEqualTo(expected);
    }

}
