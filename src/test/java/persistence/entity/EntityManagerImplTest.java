package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class EntityManagerImplTest extends DatabaseTest {

    @Test
    void find() throws SQLException {
        insertDb();
        EntityManager entityManager = new EntityManagerImpl(new H2SelectQueryBuilder(), jdbcTemplate);

        Person actual = entityManager.find(Person.class, 1L);

        assertNotNull(actual);
    }

}
