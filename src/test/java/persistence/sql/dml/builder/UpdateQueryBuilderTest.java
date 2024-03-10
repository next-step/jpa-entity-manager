package persistence.sql.dml.builder;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DummyPerson;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    private UpdateQueryBuilder queryBuilder;
    private Person person;

    @BeforeEach
    void setUp() {
        person = DummyPerson.of();
        queryBuilder = new UpdateQueryBuilder(person);
    }

    @Test
    void updateQueryByObjectTest() {
        final var expected = "UPDATE users SET nick_name = 'name', old = 10, email = 'a@a.com' WHERE id = 1;";

        final var actual = queryBuilder.build(1L);

        assertThat(actual).isEqualTo(expected);
    }

}
