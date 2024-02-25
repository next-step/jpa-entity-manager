package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.sql.Person;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.SelectAllQueryBuilder;
import persistence.sql.domain.dialect.H2Dialect;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityPersisterTest {
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect());
        String createQuery = createQueryBuilder.build(Person.class);
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(new H2Dialect());
        String dropQuery = dropQueryBuilder.build(Person.class);
        jdbcTemplate.execute(dropQuery);
    }

    @Test
    void insert() {
        //given
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate);

        //when
        entityPersister.insert(new Person(1L, "ABC", 10, "ABC@email.com", 10));
        entityPersister.insert(new Person(2L, "DEF", 20, "DEF@email.com", 20));

        //then
        String selectAllQuery = new SelectAllQueryBuilder().build(Person.class);
        List<Person> result = jdbcTemplate.query(selectAllQuery, RowMapperFactory.create(Person.class));
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void update() {
        //given
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate);

        //when
        entityPersister.insert(new Person(1L, "ABC", 10, "ABC@email.com", 10));
        String updateName = "DEF";
        int updateAge = 20;
        String updateEmail = "DEF@email.com";
        entityPersister.update(new Person(1L, updateName, updateAge, updateEmail, 20));

        //then
        EntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Person person = entityManager.find(Person.class, 1L);
        assertAll(
                () -> assertThat(person).extracting("name").isEqualTo(updateName),
                () -> assertThat(person).extracting("age").isEqualTo(updateAge),
                () -> assertThat(person).extracting("email").isEqualTo(updateEmail)
        );
    }

    @Test
    void delete() {
        //given
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate);

        //when
        Person person = new Person(1L, "ABC", 10, "ABC@email.com", 10);
        entityPersister.insert(person);
        entityPersister.delete(person);

        //then
        String selectAllQuery = new SelectAllQueryBuilder().build(Person.class);
        List<Person> result = jdbcTemplate.query(selectAllQuery, RowMapperFactory.create(Person.class));
        assertThat(result.size()).isEqualTo(0);

    }
}
