package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class EntityPersisterTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;

    private Dialect dialect;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        dialect = new FakeDialect();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).create());
    }

    @Test
    @DisplayName("엔티티가 저장이 된다.")
    void entitySave() {
        Person person = new Person("이름", 3, "dsa@gmil.com");
        EntityMeta entityMeta = new EntityMeta(person.getClass());
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityMeta, queryGenerator);

        Assertions.assertDoesNotThrow(() -> {
            entityPersister.insert(person);
        });
    }

    @Test
    @DisplayName("엔티티가 업데이트가 된다.")
    void entityUpdate() {
        Person person = new Person(1L, "이름", 3, "dsa@gmil.com");
        EntityMeta entityMeta = new EntityMeta(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityMeta, queryGenerator);

        assertThat(entityPersister.update(person)).isTrue();
    }

    @Test
    @DisplayName("엔티티가 삭제 된다.")
    void entityDelete() {
        final EntityMeta entityMeta = new EntityMeta(Person.class);
        QueryGenerator queryGenerator = QueryGenerator.of(Person.class, dialect);
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityMeta, queryGenerator);
        Person person = new Person(1L, "이름", 3, "dsa@gmil.com");

        Assertions.assertDoesNotThrow(() -> {
            entityPersister.delete(person);
        });
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        server.stop();
    }
}
