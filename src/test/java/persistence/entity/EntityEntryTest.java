package persistence.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

@DisplayName("EntityEntryTest 테스트")
class EntityEntryTest {
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
    @DisplayName("EntityEntry 저장을 진행 하고 있으면 saveing 상태가 진행 된다.")
    void saving() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        final EntityMeta entityMeta = EntityMeta.from(person.getClass());
        final QueryGenerator queryGenerator = QueryGenerator.of((entityMeta), new FakeDialect());
        final EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityMeta, queryGenerator);

        //when
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));
        final Object saved = entityEntry.saving(entityPersister, person);

        //then
        assertSoftly((it) -> {
            it.assertThat(saved).isEqualTo(person);
            it.assertThat(entityEntry.isSaving()).isTrue();
        });
    }

    @Test
    @DisplayName("엔터티를 영속화하면 manged 상태가 진행 된다.")
    void manged() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        SimplePersistenceContext simplePersistenceContext = new SimplePersistenceContext();

        //when
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));
        entityEntry.managed(simplePersistenceContext, person);

        //then
        assertSoftly((it) -> {
            it.assertThat(simplePersistenceContext.getEntity(EntityKey.of(person))).isEqualTo(person);
            it.assertThat(entityEntry.isManaged()).isTrue();
        });
    }



    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        server.stop();
    }

}
