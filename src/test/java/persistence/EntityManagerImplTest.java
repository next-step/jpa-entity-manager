package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.core.DDLExcuteor;
import persistence.core.EntityManager;
import persistence.core.EntityManagerImpl;
import persistence.entity.Person;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityManagerImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JdbcTemplate jdbcTemplate;
    private DDLExcuteor ddlExcuteor;

    private DatabaseServer server;
    EntityManager entityManager;

    @BeforeEach
    public void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlExcuteor = new DDLExcuteor(jdbcTemplate);
        entityManager = new EntityManagerImpl(server);

        createTable(Person.class);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dropTable(Person.class);
        server.stop();
    }

    private void createTable(Class<?> clazz) {
        ddlExcuteor.createTable(clazz);
    }

    private void dropTable(Class<?> clazz) {
        ddlExcuteor.dropTable(clazz);
    }


    @Test
    @DisplayName("find 실행")
    public void findTest() throws Exception {
        final Person person = new Person();
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@gmail.com");

        entityManager.persist(person);

        Person savedPerson = entityManager.find(Person.class, 1L);

        assertThat(savedPerson).isNotNull();
    }

    @Test
    @DisplayName("entity 데이터 변경후 dirtyChecking에의한 update 실행")
    public void dirtyCheckFlushTest() {
        final Person person = new Person();
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("test@gmail.com");

        entityManager.persist(person);
        Person savedPerson = entityManager.find(Person.class, 1L);
        savedPerson.setAge(33);

        entityManager.flush();

        Person updatedPerson = entityManager.find(Person.class, 1L);
        assertThat(updatedPerson.getAge()).isEqualTo(33);
    }

    @Test
    @DisplayName("entity 삭제")
    public void removeTest() {
        final Person person = new Person();
        person.setId(1L);
        person.setName("jinny");
        person.setAge(30);
        person.setEmail("");

        entityManager.persist(person);

        System.out.println("------------------------------------------- remove start ");

        entityManager.remove(person);

        assertThatThrownBy(() -> entityManager.find(Person.class, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Entity not found");
    }
}
