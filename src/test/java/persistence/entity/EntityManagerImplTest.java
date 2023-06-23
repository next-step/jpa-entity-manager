package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


class EntityManagerImplTest extends DatabaseTest {

    @Test
    void find() throws SQLException {
        insertDb();
        EntityManager entityManager = new EntityManagerImpl(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), jdbcTemplate);

        Person actual = entityManager.find(Person.class, 1L);

        assertNotNull(actual);
    }

    @Test
    void persist() throws IllegalAccessException {
        EntityManager entityManager = new EntityManagerImpl(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), jdbcTemplate);
        Person person = new Person(1L, "slow", 20, "email@email.com", 1);

        entityManager.persist(person);

        assertNotNull(entityManager.find(Person.class, 1L));
    }

    @Test
    void remove() throws IllegalAccessException {
        EntityManager entityManager = new EntityManagerImpl(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), jdbcTemplate);
        Person person = new Person(1L, "slow", 20, "email@email.com", 1);
        entityManager.persist(person);

        entityManager.remove(person);

        assertFalse(entityManager.contains(person));
    }

}
