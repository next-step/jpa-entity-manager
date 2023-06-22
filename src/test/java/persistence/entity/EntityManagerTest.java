package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.model.Person;
import persistence.sql.ddl.builder.DdlQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

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
        insert(createPerson());

        // when
        Person result = entityManager.find(Person.class, 1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("엔티티를 저장한다")
    void persist() {
        // given
        createTable(Person.class);
        Person person = createPerson();
        insert(person);

        // when
        entityManager.persist(person);

        // then
        Person result = select(Person.class, 1L);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("엔티티를 삭제한다")
    void remove() {
        // given
        createTable(Person.class);
        insert(createPerson());
        Person person = select(Person.class, 1L);

        // when
        entityManager.remove(person);

        // then
        Person result = select(Person.class, 1L);
        assertThat(result).isNull();
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

    private <T> T select(Class<T> clazz, Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(clazz);
        String findByIdQuery = selectQueryBuilder.findById(id);
        return jdbcTemplate.queryForObject(findByIdQuery, new RowMapperImpl<>(clazz));
    }
}