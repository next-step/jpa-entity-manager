package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteQueryBuilderTest {

    @DisplayName("Person객체를 통해 delete를 구현한다.")
    @Test
    void dml_delete_create() {
        Person person = new Person(1L, "simpson", 31, "qwe5507@gmail.com");
        final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(SimpleEntityMetaCreator.of(Person.class));

        String deleteQuery = deleteQueryBuilder.createDeleteQuery(person);

        String expected = "delete from users where id = 1L";
        assertThat(deleteQuery).isEqualTo(expected);
    }
}
