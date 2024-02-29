package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonFixture;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dialect.h2.H2Dialect;

@DisplayName("CustomJpaRepository class 의")
class CustomJpaRepositoryTest {

    private DatabaseServer server;

    private JdbcTemplate jdbcTemplate;
    private DdlGenerator ddlGenerator;
    private EntityManager entityManager;
    private CustomJpaRepository<Person, Long> customJpaRepository;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlGenerator = DdlGenerator.getInstance(H2Dialect.getInstance());
        entityManager = SimpleEntityManager.from(jdbcTemplate);
        jdbcTemplate.execute(ddlGenerator.generateCreateQuery(Person.class));

        customJpaRepository = new CustomJpaRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDropQuery(Person.class));
        server.stop();
    }

    @DisplayName("save 메서드는")
    @Nested
    class Save {

        @DisplayName("새로운 엔티티를 저장할 때 엔티티를 영속화한다")
        @Test
        void it_persists_new_entity() {
            // Given
            Person person = PersonFixture.createPerson();

            // When
            Person savedPerson = customJpaRepository.save(person);

            // Then
            assertNotNull(savedPerson.getId());
        }

        @DisplayName("엔티티를 병합한다")
        @Test
        void it_merges_entity() {
            // Given
            Person person = PersonFixture.createPerson();
            Person savedPerson = customJpaRepository.save(person);
            // When
            savedPerson.updateName("user2");
            Person mergedPerson = customJpaRepository.save(savedPerson);

            // Then
            assertEquals(savedPerson.getId(), mergedPerson.getId());
            assertEquals(savedPerson.getName(), mergedPerson.getName());
        }
    }
}
