package repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.impl.EntityManagerImpl;
import persistence.entity.impl.context.DefaultPersistenceContext;
import persistence.sql.ddl.generator.CreateDDLQueryGenerator;
import persistence.sql.ddl.generator.DropDDLQueryGenerator;
import persistence.sql.dialect.H2ColumnType;
import persistence.sql.dml.Database;
import persistence.sql.dml.JdbcTemplate;

@DisplayName("Repository 통합 테스트")
class CustomJpaRepositoryIntegrationTest {

    private DatabaseServer server;

    private Database jdbcTemplate;

    private EntityManager entityManager;

    private JpaRepository<TestEntity, Long> jpaRepository;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        Connection connection = server.getConnection();

        final H2ColumnType columnType = new H2ColumnType();
        entityManager = new EntityManagerImpl(connection, columnType, new DefaultPersistenceContext(columnType));

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        CreateDDLQueryGenerator createDDLQueryGenerator = new CreateDDLQueryGenerator(columnType);
        jdbcTemplate.execute(createDDLQueryGenerator.create(TestEntity.class));

        jpaRepository = new CustomJpaRepository<>(entityManager, TestEntity.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        DropDDLQueryGenerator dropDDLQueryGenerator = new DropDDLQueryGenerator(new H2ColumnType());
        jdbcTemplate.execute(dropDDLQueryGenerator.drop(TestEntity.class));
        entityManager.close();
        server.stop();
    }

    @Test
    @DisplayName("Repository를 통해 저장할 수 있다.")
    void repositoryCanSaveEntity() {
        final TestEntity entity = new TestEntity(null, "test", "test@gamil.com");
        final TestEntity savedEntity = jpaRepository.save(entity);

        assertAll(
            () -> assertThat(entity.getName()).isEqualTo(savedEntity.getName()),
            () -> assertThat(entity.getEmail()).isEqualTo(savedEntity.getEmail())
        );
    }

    @Test
    @DisplayName("Repository를 통해 캐싱된 엔티티를 불러올 수 있다.")
    void repositoryCanFindSavedEntity() {
        final TestEntity entity = new TestEntity(null, "test", "test@gamil.com");
        final TestEntity savedEntity = jpaRepository.save(entity);

        final Optional<TestEntity> findEntity = jpaRepository.findById(1L);

        assertAll(
            () -> assertThat(entity.getName()).isEqualTo(savedEntity.getName()),
            () -> assertThat(entity.getEmail()).isEqualTo(savedEntity.getEmail()),
            () -> assertThat(findEntity).hasValueSatisfying(e->
                assertThat(savedEntity == e).isTrue()
            )
        );
    }

    @Entity
    private static class TestEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private String email;

        public TestEntity(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        protected TestEntity() {

        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}