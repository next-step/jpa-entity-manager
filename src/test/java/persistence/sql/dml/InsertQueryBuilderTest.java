package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static org.assertj.core.api.Assertions.assertThat;

public class InsertQueryBuilderTest {

    private EntityMetaCreator entityMetaCreator;

    @DisplayName("Person객체를 통해 insert를 구현한다.")
    @Test
    void dml_insert_create() {
        Person person = new Person(1L, "simpson", 31, "qwe5507@gmail.com");
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
        entityMetaCreator = new SimpleEntityMetaCreator();
        final Table table = entityMetaCreator.createByInstance(person);
        String insertQuery = insertQueryBuilder.createInsertQuery(table);

        String expected = "insert into users (nick_name, old, email) values ('simpson', 31, 'qwe5507@gmail.com')";
        assertThat(insertQuery).isEqualTo(expected);
    }
}
