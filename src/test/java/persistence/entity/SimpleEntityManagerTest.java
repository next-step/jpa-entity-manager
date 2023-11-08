package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jakarta.persistence.EntityNotFoundException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.H2DdlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleEntityManagerTest {
    private final static Person person = new Person("name", 1, "test@email.com", 1);

    public static EntityManager entityManager;

    @BeforeAll
    static void setJdbcTemplate() throws SQLException {
        DatabaseServer server = new H2();
        server.start();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

        EntityMetadata entityMetadata = new EntityMetadata(new Person());

        jdbcTemplate.execute(H2DdlQueryBuilder.build().dropQuery(entityMetadata));
        jdbcTemplate.execute(H2DdlQueryBuilder.build().createQuery(entityMetadata));

        entityManager = new SimpleEntityManager(new EntityPersister(jdbcTemplate), new EntityLoader(jdbcTemplate), new SimplePersistenceContext());
        entityManager.persist(new Person("hhhhhwi", 1, "aab555586@gmail.com", 0));
    }

    @DisplayName("EnityManager를 통해 PK 값이 일치하는 Entity를 찾는다.")
    @Test
    void test_find() {
        Person resultPerson = entityManager.find(Person.class, 1L);

        assertEquals(new Person(1L, "hhhhhwi",1,"aab555586@gmail.com", 0), resultPerson);
    }

    @DisplayName("EntityManager를 통해 Entity를 저장한다.")
    @Test
    void test_persist() {
        entityManager.persist(person);
        Person resultPerson = entityManager.find(Person.class, 2L);

        assertEquals(new Person(2L, "name", 1, "test@email.com", 1), resultPerson);
    }

    @DisplayName("EntityManager를 통해 Entity를 삭제한다.")
    @Test
    void test_remove() {
        entityManager.remove(new Person(1L, "hhhhhwi", 1, "aab555586@gmail.com", 0));

        assertThrows(EntityNotFoundException.class,
                () -> entityManager.find(Person.class, 1L));
    }
}
