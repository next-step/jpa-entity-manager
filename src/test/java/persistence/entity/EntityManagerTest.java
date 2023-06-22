package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.model.Person;
import persistence.sql.ddl.builder.DdlQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;

import java.sql.SQLException;

import static fixture.PersonFixtures.createPerson;
import static org.assertj.core.api.Assertions.assertThat;

class EntityManagerTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityManager = new BasicEntityManger(jdbcTemplate);
    }

    @Test
    @DisplayName("엔티티를 조회한다")
    void find() {
        // given
        createTable(Person.class);

        Person person = createPerson();
        insert(person);

        // when
        Person result = entityManager.find(Person.class, 1L);

        // then
        assertThat(result.getName()).isEqualTo(person.getName());
    }

    private void createTable(Class<?> clazz) {
        DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(clazz);
        String createQuery = ddlQueryBuilder.create();
        jdbcTemplate.execute(createQuery);
    }

    private void insert(Object object) {
        String insertQuery = InsertQueryBuilder.INSTANCE.insert(object);
        jdbcTemplate.execute(insertQuery);
    }
}