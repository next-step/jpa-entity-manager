package hibernate.entity;

import database.DatabaseServer;
import database.H2;
import hibernate.EntityEntry;
import hibernate.ddl.CreateQueryBuilder;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.persistencecontext.EntityKey;
import hibernate.entity.persistencecontext.EntitySnapshot;
import hibernate.entity.persistencecontext.SimplePersistenceContext;
import jakarta.persistence.*;
import jdbc.JdbcTemplate;
import jdbc.ReflectionRowMapper;
import jdbc.RowMapper;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static hibernate.Status.MANAGED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityManagerImplTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private EntityManagerImpl entityManager;
    private Map<EntityKey, Object> persistenceContextEntities;
    private Map<Object, EntityEntry> entityEntryContextEntities;
    private Map<EntityKey, EntitySnapshot> persistenceContextSnapshotEntities;
    private static final CreateQueryBuilder createQueryBuilder = CreateQueryBuilder.INSTANCE;

    @BeforeEach
    void beforeEach() {
        persistenceContextEntities = new ConcurrentHashMap<>();
        persistenceContextSnapshotEntities = new ConcurrentHashMap<>();
        entityEntryContextEntities = new ConcurrentHashMap<>();
        entityManager = new EntityManagerImpl(
                new EntityPersister(jdbcTemplate),
                new EntityLoader(jdbcTemplate),
                new SimplePersistenceContext(persistenceContextEntities, persistenceContextSnapshotEntities, new EntityEntryContext(entityEntryContextEntities))
        );
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
    void 저장된_객채를_찾은_후_PersistenceContext에_저장한다() {
        // given
        jdbcTemplate.execute("insert into test_entity (id, nick_name, age) values (1, '최진영', 19)");

        // when
        TestEntity actual = entityManager.find(TestEntity.class, 1L);

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("최진영"),
                () -> assertThat(actual.age).isEqualTo(19),
                () -> assertThat(persistenceContextEntities).containsValue(actual)
        );
    }

    @Test
    void 저장된_객체가_PersistenceContext에_있을_경우_바로_꺼내온다() {
        // given
        TestEntity givenEntity = new TestEntity();
        persistenceContextEntities.put(new EntityKey(1L, TestEntity.class), givenEntity);
        entityEntryContextEntities.put(givenEntity, new EntityEntry(MANAGED));

        // when
        TestEntity actual = entityManager.find(TestEntity.class, 1L);

        // then
        assertThat(actual).isEqualTo(givenEntity);
    }

    @Test
    void 객체를_persist한_후_entity에_삽입하고_PersistenceContext에_넣는다() {
        // given
        TestEntity givenEntity = new TestEntity("최진영", 19, "jinyoungchoi95@gmail.com");

        // when
        entityManager.persist(givenEntity);
        TestEntity actual = jdbcTemplate.queryForObject("select id, nick_name, age from test_entity;", ReflectionRowMapper.getInstance(EntityClass.getInstance(TestEntity.class)));
        TestEntity actualPersistenceContext = (TestEntity) persistenceContextEntities.get(new EntityKey(actual.id, TestEntity.class));

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo(givenEntity.name),
                () -> assertThat(actual.age).isEqualTo(givenEntity.age),
                () -> assertThat(actualPersistenceContext.id).isEqualTo(1L)
        );
    }

    @Test
    void 이미_persist된_객체를_저장하려하는_경우_예외가_발생한다() {
        // given
        TestEntity givenEntity = new TestEntity(1L, "최진영", 19, "jinyoungchoi95@gmail.com");
        persistenceContextEntities.put(new EntityKey(1L, TestEntity.class), givenEntity);

        // when & then
        assertThatThrownBy(() -> entityManager.persist(givenEntity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 영속화되어있는 entity입니다.");
    }

    @Test
    void 객체를_영속성_제거_및_db에_제거한다() {
        // given
        TestEntity givenEntity = new TestEntity(1L, "최진영", 19, "jinyoungchoi95@gmail.com");
        entityManager.persist(givenEntity);

        // when
        entityManager.remove(givenEntity);
        Integer actual = jdbcTemplate.queryForObject("select count(*) from test_entity", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet) {
                try {
                    return resultSet.getInt(1);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // then
        assertAll(
                () -> assertThat(actual).isEqualTo(0),
                () -> assertThat(persistenceContextEntities).isEmpty()
        );
    }

    @Test
    void id가_없는_entity가_merge하는_경우_예외가_발생한다() {
        TestEntity givenEntity = new TestEntity(null, "영진최", 19, "jinyoungchoi95@gmail.com");
        assertThatThrownBy(() -> entityManager.merge(givenEntity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("id가 없는 entity는 merge할 수 없습니다.");
    }

    @Test
    void merge할_때_기존_필드와_다르면_업데이트된다() {
        // given
        TestEntity givenEntity = new TestEntity(1L, "영진최", 19, "jinyoungchoi95@gmail.com");
        entityManager.persist(new TestEntity(1L, "최진영", 19, "jinyoungchoi95@gmail.com"));

        // when
        entityManager.merge(givenEntity);
        TestEntity actual = findTestEntity();

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("영진최"),
                () -> assertThat(actual.age).isEqualTo(19),
                () -> assertThat(persistenceContextEntities.values()).contains(givenEntity),
                () -> assertThat(persistenceContextSnapshotEntities.values().stream().findAny().get().getSnapshot().values().contains("영진최")).isTrue()
        );
    }

    @Test
    void merge할_때_영속화되어있지_않아도_기존_필드와_다르면_업데이트된다() {
        // given
        TestEntity givenEntity = new TestEntity(1L, "영진최", 19, "jinyoungchoi95@gmail.com");
        jdbcTemplate.execute("insert into test_entity (id, nick_name, age) values (1, '최진영', 19)");

        // when
        entityManager.merge(givenEntity);
        TestEntity actual = findTestEntity();

        // then
        assertAll(
                () -> assertThat(actual.id).isEqualTo(1L),
                () -> assertThat(actual.name).isEqualTo("영진최"),
                () -> assertThat(actual.age).isEqualTo(19),
                () -> assertThat(persistenceContextEntities.values()).contains(givenEntity),
                () -> assertThat(persistenceContextSnapshotEntities.values().stream().findAny().get().getSnapshot().values().contains("영진최")).isTrue()
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

        public TestEntity(Long id, String name, Integer age, String email) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public TestEntity(String name, Integer age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
    }
}
