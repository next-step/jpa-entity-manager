package jpa;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.sql.Dialect;
import persistence.sql.H2Dialect;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.ddl.Person;
import persistence.sql.ddl.QueryBuilder;
import sql.ddl.JdbcServerExtension;
import sql.ddl.JdbcServerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcServerTest
class EntityLoaderTest {

    private static final JdbcTemplate jdbcTemplate = JdbcServerExtension.getJdbcTemplate();
    private static final Dialect dialect = new H2Dialect();
    private static final EntityLoader entityLoader = new EntityLoader(jdbcTemplate);
    private static final EntityPersister entityPersister = new EntityPersisterImpl(jdbcTemplate);

    @BeforeEach
    void setUp() {
        Class<Person> clazz = Person.class;
        QueryBuilder createQueryBuilder = new CreateQueryBuilder(clazz, dialect);
        jdbcTemplate.execute(createQueryBuilder.build());
    }

    @Test
    void 조회() {
        String name = "이름";
        int age = 10;
        String email = "jsss@test.com";
        int index = 1;
        long id = 1;
        Person person = new Person(name, age, email, index);
        entityPersister.insert(person);

        Person findPerson = entityLoader.find(Person.class, id);

        assertAll(() -> {
            assertThat(findPerson.getAge()).isEqualTo(age);
            assertThat(findPerson.getEmail()).isEqualTo(email);
            assertThat(findPerson.getId()).isEqualTo(id);
        });
    }

    @AfterEach
    void tearDown() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(Person.class);
        jdbcTemplate.execute(dropQueryBuilder.build());
    }
}
