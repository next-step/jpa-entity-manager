package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    private EntityMetaCreator entityMetaCreator;

    @DisplayName("Person객체를 통해 update 기능을 구현한다.")
    @Test
    void dml_findAll_create() {
        Person person = new Person(1L, "simpson", 31, "qwe5507@gmail.com");
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
        entityMetaCreator = new SimpleEntityMetaCreator();
        final Table table = entityMetaCreator.createByInstance(person);
        String findAllQuery = updateQueryBuilder.createUpdateQuery(table);

        String expected = "update users set nick_name='simpson', old=31, email='qwe5507@gmail.com' where id=1";
        assertThat(findAllQuery).isEqualTo(expected);
    }
}