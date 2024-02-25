package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.dml.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class SelectQueryBuilderTest {

    @DisplayName("Person객체를 통해 findAll 기능을 구현한다.")
    @Test
    void dml_findAll_create() {
        Person person = new Person("simpson", 31, "qwe5507@gmail.com");
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(person.getClass(), new H2Dialect());

        String findAllQuery = selectQueryBuilder.createFindAllQuery();

        String expected = "select id, nick_name, old, email from users";
        assertThat(findAllQuery).isEqualTo(expected);
    }

    @DisplayName("Person객체를 통해 findById 기능을 구현한다.")
    @Test
    void dml_findById_create() {
        Person person = new Person(1L, "simpson", 31, "qwe5507@gmail.com");
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(person.getClass(), new H2Dialect());

        String findByIdQuery = selectQueryBuilder.createFindByIdQuery(1L);

        String expected = "select id, nick_name, old, email from users where id = 1L";
        assertThat(findByIdQuery).isEqualTo(expected);
    }
}