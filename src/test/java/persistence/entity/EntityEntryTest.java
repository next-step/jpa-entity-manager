package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.exception.ObjectNotFoundException;
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
        });
    }

    @Test
    @DisplayName("엔터티를 영속화하면 manged 상태가 진행 된다.")
    void manged() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));

        //when
        entityEntry.managed();

        //then
        assertThat(entityEntry.isManaged()).isTrue();
    }

    @Test
    @DisplayName("EntityEntry가 리드온리 상태면 저장이 되지 않는다")
    void readOnly() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));
        final EntityMeta entityMeta = EntityMeta.from(person.getClass());
        final QueryGenerator queryGenerator = QueryGenerator.of((entityMeta), new FakeDialect());
        final EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entityMeta, queryGenerator);

        //when
        entityEntry.readOnly();

        //then
        assertThatIllegalStateException().isThrownBy(
                () -> entityEntry.saving(entityPersister, person)
        );
    }

    @Test
    @DisplayName("EntityEntry가 deleted상태가 된다")
    void deleted() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));

        //when
        entityEntry.deleted();

        //then
        assertTrue(entityEntry.isDeleted());
    }

    @Test
    @DisplayName("엔터티가 ReadOnly면 삭제가 진행되지 않는다.")
    void readOnlyDelete() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));

        //when
        entityEntry.readOnly();

        //then
        assertThatIllegalStateException()
                .isThrownBy(entityEntry::deleted);
    }

    @Test
    @DisplayName("gone 상태이면 로드가 되지 않는다.")
    void goneIsNotLoad() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));
        final EntityMeta entityMeta = EntityMeta.from(person.getClass());
        final QueryGenerator queryGenerator = QueryGenerator.of((entityMeta), new FakeDialect());
        EntityLoader entityLoader = new EntityLoader(jdbcTemplate, queryGenerator, new EntityMapper(entityMeta));

        //when
        entityEntry.gone();

        //then
        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> entityEntry.loading(entityLoader, Person.class, 1L));
    }

    @Test
    @DisplayName("EntityEntry의 초기 상태는 Loading 상태이다")
    void initLoading() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        EntityEntry entityEntry = new EntityEntry(EntityKey.of(person));

        //when & then
        assertThat(entityEntry.isLoading()).isTrue();
    }


    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        server.stop();
    }

}
