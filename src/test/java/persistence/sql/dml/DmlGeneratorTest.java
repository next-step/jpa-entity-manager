package persistence.sql.dml;


import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.core.EntityMetadata;
import persistence.core.EntityMetadataProvider;
import persistence.dialect.Dialect;
import persistence.dialect.h2.H2Dialect;
import persistence.util.ReflectionUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DmlGeneratorTest {

    private EntityMetadata<?> entityMetadata;
    private DmlGenerator generator;
    private Person person;

    @BeforeEach
    void setUp() {
        final Dialect dialect = new H2Dialect();
        this.generator = new DmlGenerator(dialect);
        this.person = new Person(1L, "min", 30, "jongmin4943@gmail.com");
        this.entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(Person.class);
    }

    @Test
    @DisplayName("주어진 person 인스턴스를 이용해 insert ddl 을 생성할 수 있다.")
    void dmlGeneratorTest() {
        final List<String> columnNames = entityMetadata.getInsertableColumnNames();
        final List<Object> values = ReflectionUtils.getFieldValues(person, entityMetadata.getInsertableColumnFieldNames());
        final String query = generator.insert(entityMetadata.getTableName(), columnNames, values);

        assertThat(query).isEqualToIgnoringCase(String.format("insert into users (nick_name, old, email) values ('%s', %d, '%s')", "min", 30, "jongmin4943@gmail.com"));
    }

    @Test
    @DisplayName("Person 클래스 정보로 select ddl 을 생성할 수 있다.")
    void findAllTest() {
        final String query = generator.findAll(entityMetadata.getTableName(), entityMetadata.getColumnNames());

        assertThat(query).isEqualToIgnoringCase("select id, nick_name, old, email from users");
    }

    @Test
    @DisplayName("Person 클래스 정보로 select ddl 을 생성할 수 있다.")
    void findByIdTest() {
        final String query = generator.findById(entityMetadata.getTableName(), entityMetadata.getColumnNames(), entityMetadata.getIdColumnName(), person.getId());

        assertThat(query).isEqualToIgnoringCase("select id, nick_name, old, email from users where id=1");
    }

    @Test
    @DisplayName("Person 클래스 정보로 delete ddl 을 생성할 수 있다.")
    void generateDeleteDmlTest() {
        final String query = generator.delete(entityMetadata.getTableName(), entityMetadata.getIdColumnName(), person.getId());

        assertThat(query).isEqualToIgnoringCase("delete from users where id=1");
    }

    @Test
    @DisplayName("Person 클래스 정보로 delete ddl 을 생성할 수 있다.")
    void generateUpdateDmlTest() {
        final List<String> columnNames = entityMetadata.getInsertableColumnNames();
        final List<Object> values = ReflectionUtils.getFieldValues(person, entityMetadata.getInsertableColumnFieldNames());
        final String query = generator.update(entityMetadata.getTableName(), columnNames, values, entityMetadata.getIdColumnName(), person.getId());

        assertThat(query).isEqualToIgnoringCase("update users set nick_name='min', old=30, email='jongmin4943@gmail.com' where id=1");
    }

}
