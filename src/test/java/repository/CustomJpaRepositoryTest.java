package repository;

import database.DatabaseServer;
import database.H2;
import hibernate.ddl.CreateQueryBuilder;
import hibernate.entity.EntityEntryContext;
import hibernate.entity.EntityLoader;
import hibernate.entity.EntityManagerImpl;
import hibernate.entity.EntityPersister;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.persistencecontext.EntityKey;
import hibernate.entity.persistencecontext.EntitySnapshot;
import hibernate.entity.persistencecontext.SimplePersistenceContext;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CustomJpaRepositoryTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private EntityManagerImpl entityManager;
    private Map<EntityKey, Object> persistenceContextEntities;
    private Map<EntityKey, EntitySnapshot> persistenceContextSnapshotEntities;
    private CustomJpaRepository<TestEntity, Long> customJpaRepository;
    private static final CreateQueryBuilder createQueryBuilder = CreateQueryBuilder.INSTANCE;

    @BeforeEach
    void beforeEach() {
        persistenceContextEntities = new ConcurrentHashMap<>();
        persistenceContextSnapshotEntities = new ConcurrentHashMap<>();
        entityManager = new EntityManagerImpl(
                new EntityPersister(jdbcTemplate),
                new EntityLoader(jdbcTemplate),
                new SimplePersistenceContext(persistenceContextEntities, persistenceContextSnapshotEntities, new EntityEntryContext(new ConcurrentHashMap<>()))
        );
        customJpaRepository = new CustomJpaRepository<>(entityManager);
    }

    @BeforeAll
    static void beforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(createQueryBuilder.generateQuery(EntityClass.getInstance(TestEntity.class)));
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("truncate table test_entity;");
    }

    @AfterAll
    static void afterAll() {
        jdbcTemplate.execute("drop table test_entity;");
        server.stop();
    }

    @Test
    void 새로운_entity의_경우_저장한다() {
        customJpaRepository.save(new TestEntity("최진영", 19, "jinyoungchoi95@gmail.com"));
        TestEntity actual = findTestEntity();
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("최진영"),
                () -> assertThat(actual.age).isEqualTo(19)
        );
    }

    @Test
    void 영속화된_entity인_경우_update한다() {
        // given
        EntityKey givenEntityKey = new EntityKey(1L, TestEntity.class);
        TestEntity givenEntity = new TestEntity(1L, "최진영", 19);
        persistenceContextEntities.put(givenEntityKey, givenEntity);
        persistenceContextSnapshotEntities.put(givenEntityKey, new EntitySnapshot(givenEntity));
        jdbcTemplate.execute("insert into test_entity (id, nick_name, age) values (1, '최진영', 19)");

        // when
        customJpaRepository.save(new TestEntity(1L, "진영최", 19));
        TestEntity actual = findTestEntity();

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("진영최"),
                () -> assertThat(actual.age).isEqualTo(19)
        );
    }

    private TestEntity findTestEntity() {
        return jdbcTemplate.queryForObject("select id, nick_name, age from test_entity", new RowMapper<TestEntity>() {
            @Override
            public TestEntity mapRow(ResultSet resultSet) throws SQLException {
                return new TestEntity(
                        resultSet.getLong("id"),
                        resultSet.getString("nick_name"),
                        resultSet.getInt("age")
                );
            }
        });
    }

    @Entity
    @Table(name = "test_entity")
    static class TestEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "nick_name", nullable = false)
        private String name;

        private Integer age;

        @Transient
        private String email;

        public TestEntity() {
        }

        public TestEntity(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public TestEntity(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
    }
}
