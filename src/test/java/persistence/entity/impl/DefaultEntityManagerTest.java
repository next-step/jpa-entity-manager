package persistence.entity.impl;

import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityRowMapper;
import persistence.sql.ddl.CreateTableQueryBuilder;
import persistence.sql.ddl.DropTableQueryBuilder;
import persistence.sql.ddl.QueryBuilder;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class DefaultEntityManagerTest {
    private DefaultEntityManager entityManager;
    private JdbcTemplate jdbcTemplate;
    private Connection connection;

    @BeforeEach
    public void setUp() throws Exception {
        H2 databaseServer = new H2();
        databaseServer.start();
        connection = databaseServer.getConnection();
        jdbcTemplate = new JdbcTemplate(connection);
        entityManager = new DefaultEntityManager(jdbcTemplate);

        // Create table for Person
        QueryBuilder ddlQueryBuilder = new CreateTableQueryBuilder(Person.class);
        String createTableQuery = ddlQueryBuilder.executeQuery();
        jdbcTemplate.execute(createTableQuery);
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Drop table for Person
        QueryBuilder ddlQueryBuilder = new DropTableQueryBuilder(Person.class);
        String dropTableQuery = ddlQueryBuilder.executeQuery();
        jdbcTemplate.execute(dropTableQuery);
        connection.close();
    }

    @Test
    public void testPersist() {
        Person person = Person.of(null, "John", 25, "john@example.com", 1);
        Person persistedPerson = (Person) entityManager.persist(person);
        assertNotNull(persistedPerson);
    }

    @Test
    public void testFind() {
        Person person = Person.of(null, "John", 25, "john@example.com", 1);
        entityManager.persist(person);
        Optional<Person> foundedPerson = entityManager.find(Person.class, 1L);
        assertTrue(foundedPerson.isPresent());
    }

    @Test
    public void testRemove() {
        Person person = Person.of(null, "John", 25, "john@example.com", 1);
        entityManager.persist(person);
        entityManager.remove(Person.class, 1L);
        Optional<Person> foundedPerson = entityManager.find(Person.class, 1L);


        assertTrue(foundedPerson.isEmpty());
    }

    @Test
    public void testUpdate() {
        Person person = Person.of(null, "John", 25, "john@example.com", 1);
        entityManager.persist(person);
        Person updatedPerson = Person.of(1L, "John Updated", 26, "john.updated@example.com", 1);
        entityManager.update(updatedPerson);
        Optional<Person> foundedPerson = entityManager.find(Person.class, 1L);
        assertTrue(foundedPerson.isPresent());

        Person person2 = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = 1", new EntityRowMapper<>(Person.class));
        Person person1 = foundedPerson.get();
        assertNotEquals(person1, person2);
        assertNotEquals(person1.toString(), person2.toString());
    }

    @Test
    public void testFlush() throws NoSuchFieldException {
        Person person = Person.of(null, "John", 25, "john@example.com", 1);
        entityManager.persist(person);
        Person updatedPerson = Person.of(1L, "John Updated", 26, "john.updated@example.com", 1);
        entityManager.update(updatedPerson);

        Optional<Person> foundedPerson = entityManager.find(Person.class, 1L);
        assertTrue(foundedPerson.isPresent());
        entityManager.flush();
        Person person2 = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = 1", new EntityRowMapper<>(Person.class));
        Person person1 = foundedPerson.get();
        assertNotEquals(person1, person2);
    }
}