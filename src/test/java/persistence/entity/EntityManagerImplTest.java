package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.ddl.view.mysql.MySQLPrimaryKeyResolver;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.entity.Person;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerImplTest {

    private static DatabaseServer server;
    private static EntityManager entityManager;

    @BeforeAll
    static void initDatabase() throws SQLException {
        server = new H2();
        server.start();
        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(new MySQLPrimaryKeyResolver());
        Connection connection = server.getConnection();
        new JdbcTemplate(connection).execute(ddlQueryBuilder.createQuery(Person.class));
        entityManager = new EntityManagerImpl(connection, new DmlQueryBuilder());
    }

    @AfterAll
    static void destroy() {
        server.stop();
    }

    @Test
    @DisplayName("entity manager integration test")
    void should_remove_entity() {
        Long id = 1L;
        Person person = new Person(id,"cs", 29, "katd216@gmail.com", 0);
        entityManager.persist(person);
        Person foundPerson = entityManager.find(Person.class, id);
        entityManager.remove(foundPerson);

        assertThat(entityManager.find(Person.class, id)).isNull();
    }

    @Test
    void should_update_entity(){
        Long id = 2L;
        String updateName = "test";
        Integer updateAge = 32;
        String updateEmail = "katd6@naver.com";
        Person person = new Person(id,"cs", 29, "katd216@gmail.com", 0);
        entityManager.persist(person);
        entityManager.update(new Person(updateName,updateAge, updateEmail,2), id);

        Person updatePerson = entityManager.find(Person.class, id);

        assertAll(
                () -> validateFieldValue(Person.class, "name", updateName, updatePerson),
                () -> validateFieldValue(Person.class, "age", updateAge, updatePerson),
                () -> validateFieldValue(Person.class, "email", updateEmail, updatePerson)
        );
    }

    private void validateFieldValue(Class<?> clazz, String fieldName, Object fieldValue, Object instance) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        assertThat(field.get(instance)).isEqualTo(fieldValue);
    }

}
