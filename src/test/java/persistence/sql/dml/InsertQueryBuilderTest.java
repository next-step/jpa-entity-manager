package persistence.sql.dml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.Columns;
import persistence.sql.meta.TableName;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static org.assertj.core.api.Assertions.assertThat;

public class InsertQueryBuilderTest {

    private TableName tableName;
    private Columns columns;

    @BeforeEach
    void init() {
        final SimpleEntityMetaCreator entityMetaCreator = SimpleEntityMetaCreator.of(Person.class);
        tableName = entityMetaCreator.createTableName();
        columns = entityMetaCreator.createColumns();
    }

    @DisplayName("Person객체를 통해 insert를 구현한다.")
    @Test
    void dml_insert_create() {
        Person person = new Person(1L, "simpson", 31, "qwe5507@gmail.com");
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(tableName, columns);

        String insertQuery = insertQueryBuilder.createInsertQuery(person);

        String expected = "insert into users (nick_name, old, email) values ('simpson', 31, 'qwe5507@gmail.com')";
        assertThat(insertQuery).isEqualTo(expected);
    }
}
