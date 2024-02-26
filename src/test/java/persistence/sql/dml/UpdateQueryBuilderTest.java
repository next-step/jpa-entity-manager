package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.dml.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    @DisplayName("Person객체를 통해 findAll 기능을 구현한다.")
    @Test
    void dml_findAll_create() {
        Person person = new Person(1L, "simpson", 31, "qwe5507@gmail.com");
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(Person.class, new H2Dialect());

        String findAllQuery = updateQueryBuilder.createUpdateQuery(person);

        String expected = "update users set nick_name='simpson', old=31, email='qwe5507@gmail.com' where id=1L";
        assertThat(findAllQuery).isEqualTo(expected);
    }
}