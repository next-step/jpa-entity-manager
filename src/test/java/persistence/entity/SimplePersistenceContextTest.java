package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import java.util.List;
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

class SimplePersistenceContextTest {

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
    @DisplayName("영속성 컨텍스트에 의해 저장 및 조회가 된다.")
    void getEntity() {
        //given
        Person person = new Person(1L ,"이름", 19, "asd@gmail.com");
        PersistenceContext context = new SimplePersistenceContext();
        final EntityKey entityKey = EntityKey.of(person);

        //when
        context.addEntity(entityKey, person);

        //then
        assertSoftly((it) -> {
            it.assertThat(context.getEntity(entityKey)).isEqualTo(person);
            it.assertThat(context.getEntity(entityKey) == person).isTrue();
        });
    }

    @Test
    @DisplayName("영속성 컨텍스트에서 삭제가 된다.")
    void removeEntity() {
        //given
        Person person = new Person(1L ,"이름", 19, "asd@gmail.com");
        PersistenceContext context = new SimplePersistenceContext();
        final EntityKey entityKey = EntityKey.of(person);

        //when
        context.addEntity(entityKey, person);
        context.removeEntity(person);

        //then
        assertThat(context.getEntity(entityKey)).isNull();
    }

    @Test
    @DisplayName("영속성 컨텍스트에서 스냅샷을 만들어서 저장한다.")
    void getDatabaseSnapshot() {
        //given
        Person person = new Person(1L, "이름", 19, "sad@gmail.com");
        PersistenceContext context = new SimplePersistenceContext();
        final EntityKey entityKey = EntityKey.of(person);


        //when
        context.addEntity(entityKey, person);
        context.getDatabaseSnapshot(entityKey, person);
        person.changeEmail("변경이메일@gamil.com");
        final Person entity = (Person) context.getEntity(entityKey);
        final Person snapshot = (Person) context.getDatabaseSnapshot(entityKey, person);

        //then
        assertSoftly((it) -> {
            it.assertThat(snapshot.getEmail()).isNotEqualTo("변경이메일@gamil.com");
            it.assertThat(entity.getEmail()).isNotEqualTo(snapshot.getEmail());
            it.assertThat(entity).isNotEqualTo(snapshot);
        });
    }

    @Test
    @DisplayName("영속성 컨텍스트에서 변경된 엔티티를 조회한다.")
    void getChangedEntity() {
        //given
        Person person = new Person(1L, "이름", 19, "sad@gmail.com");
        Person person2 = new Person(2L, "이름", 19, "sad@gmail.com");
        SimplePersistenceContext context = new SimplePersistenceContext();
        final EntityKey entityKey = EntityKey.of(person);
        final EntityKey entityKey2 = EntityKey.of(person2);


        //when
        context.addEntity(entityKey, person);
        context.addEntity(entityKey2, person2);
        person.changeEmail("변경이메일@gamil.com");

        //then.
        assertSoftly((it -> {
            it.assertThat(context.getChangedEntity()).hasSize(1);
            it.assertThat(context.getChangedEntity()).contains(person);
            it.assertThat(context.getChangedEntity()).doesNotContain(person2);
        }));
    }

    @Test
    @DisplayName("엔터티를 저장한다.")
    void saving() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        SimplePersistenceContext simplePersistenceContext = new SimplePersistenceContext();
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, EntityMeta.from(Person.class), QueryGenerator.of(Person.class, dialect));

        //when
        simplePersistenceContext.saving(entityPersister, person);


        //then
        assertThat(simplePersistenceContext.getEntity(EntityKey.of(person))).isEqualTo(person);
    }

    @Test
    @DisplayName("엔터티를 삭제한다")
    void deleted() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        SimplePersistenceContext simplePersistenceContext = new SimplePersistenceContext();
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, EntityMeta.from(Person.class), QueryGenerator.of(Person.class, dialect));

        //when
        simplePersistenceContext.saving(entityPersister, person);
        simplePersistenceContext.deleted(person);

        //then
        assertThat(simplePersistenceContext.getEntity(EntityKey.of(person))).isNull();
    }

    @Test
    @DisplayName("엔티티를 로딩한다.")
    void load() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        SimplePersistenceContext simplePersistenceContext = new SimplePersistenceContext();
        EntityLoader entityLoader =
                new EntityLoader(jdbcTemplate,QueryGenerator.of(Person.class, dialect),  new EntityMapper(EntityMeta.from(person.getClass()))
                );

        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, EntityMeta.from(Person.class), QueryGenerator.of(Person.class, dialect));

        //when
        simplePersistenceContext.saving(entityPersister, person);
        final Person loading = simplePersistenceContext.loading(entityLoader, Person.class, 1L);

        //then
        assertThat(loading).isEqualTo(person);
    }

    @Test
    @DisplayName("엔티티들을 로딩한다.")
    void loadAll() {
        //given
        Person person = new Person(1L, "이름", 30, "email@odna");
        Person person2 = new Person(2L, "이름", 30, "email@odna");
        SimplePersistenceContext simplePersistenceContext = new SimplePersistenceContext();
        EntityLoader entityLoader =
                new EntityLoader(jdbcTemplate,QueryGenerator.of(Person.class, dialect),  new EntityMapper(EntityMeta.from(person.getClass()))
                );

        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, EntityMeta.from(Person.class), QueryGenerator.of(Person.class, dialect));

        //when
        simplePersistenceContext.saving(entityPersister, person);
        simplePersistenceContext.saving(entityPersister, person2);
        final List<Person> loading = simplePersistenceContext.findAll(entityLoader, Person.class);

        //then
        assertThat(loading).contains(person, person2);
    }
    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).drop());
        server.stop();
    }

}
