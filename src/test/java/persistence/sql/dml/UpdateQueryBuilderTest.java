package persistence.sql.dml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    private String tableName;
    private PrimaryKey primaryKey;
    private Columns columns;

    @BeforeEach
    void init() {
        final SimpleEntityMetaCreator entityMetaCreator = SimpleEntityMetaCreator.of(Person.class);
        tableName = entityMetaCreator.createTableName();
        primaryKey = entityMetaCreator.createPrimaryKey();
        columns = entityMetaCreator.createColumns();
    }

    @DisplayName("Person객체를 통해 update 기능을 구현한다.")
    @Test
    void dml_findAll_create() {
        Person person = new Person(1L, "simpson", 31, "qwe5507@gmail.com");
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(tableName, primaryKey, columns);

        String findAllQuery = updateQueryBuilder.createUpdateQuery(person);

        String expected = "update users set nick_name='simpson', old=31, email='qwe5507@gmail.com' where id=1L";
        assertThat(findAllQuery).isEqualTo(expected);
    }
}