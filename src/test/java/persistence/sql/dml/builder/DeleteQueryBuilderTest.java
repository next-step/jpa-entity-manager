package persistence.sql.dml.builder;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DummyPerson;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    private DeleteQueryBuilder queryBuilder;
    private Person person;

    @BeforeEach
    void setUp() {
        person = DummyPerson.ofNullId();
        queryBuilder = new DeleteQueryBuilder(person);
    }

    @Test
    void deleteQueryByObjectTest() {
        final var expected = "DELETE FROM users WHERE nick_name = 'name' AND old = 10 AND email = 'a@a.com';";

        final var actual = queryBuilder.build();

        assertThat(actual).isEqualTo(expected);
    }

}
