package jpa;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.sql.Dialect;
import persistence.sql.H2Dialect;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.Person;
import persistence.sql.ddl.QueryBuilder;
import persistence.sql.model.EntityColumnValue;
import sql.ddl.JdbcServerExtension;
import sql.ddl.JdbcServerTest;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcServerTest
class EntityPersisterImplTest {

    private static final Dialect dialect = new H2Dialect();
    private static final JdbcTemplate jdbcTemplate = JdbcServerExtension.getJdbcTemplate();
    private static final EntityPersister entityPersister = new EntityPersisterImpl(new PersistenceContextImpl(), jdbcTemplate);

    @BeforeEach
    void setUp() {
        Class<Person> clazz = Person.class;
        QueryBuilder createQueryBuilder = new CreateQueryBuilder(clazz, dialect);
        jdbcTemplate.execute(createQueryBuilder.build());
    }

    @Test
    void 업데이트() throws NoSuchFieldException {
        Person person = new Person("테스터", 20, "test@email.com", 1);
        entityPersister.insert(person);

        Person insertedPerson = entityPersister.find(Person.class, 1L);
        String updateEmail = "test@naver.com";
        insertedPerson.setEmail(updateEmail);
        entityPersister.update(insertedPerson);


        Person updatedPerson = entityPersister.find(Person.class, 1L);

        Field emailField = updatedPerson.getClass().getDeclaredField("email");
        EntityColumnValue emailColumnValue = new EntityColumnValue(emailField, insertedPerson);

        assertThat(emailColumnValue.getValue()).isEqualTo(updateEmail);
    }

    @Test
    void 데이터_삽입_및_조회() throws NoSuchFieldException {
        String name = "이름";
        int age = 10;
        String email = "jsss@test.com";
        int index = 1;
        Person person = new Person(name, age, email, index);
        entityPersister.insert(person);

        Person savedPerson = entityPersister.find(Person.class, 1L);
        Field nameField = savedPerson.getClass().getDeclaredField("name");

        EntityColumnValue entityColumnValue = new EntityColumnValue(nameField, savedPerson);

        assertThat(entityColumnValue.getValue()).isEqualTo(name);
    }

    @Test
    void 삭제() {
        String name = "이름";
        int age = 10;
        String email = "js12321ss@test.com";
        int index = 1;
        Person person = new Person(name, age, email, index);
        entityPersister.insert(person);

        entityPersister.delete(person);
    }


}
