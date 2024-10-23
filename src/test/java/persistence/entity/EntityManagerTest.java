package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
import persistence.model.exception.ColumnInvalidException;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.fixture.PersonWithTransientAnnotation;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class EntityManagerTest {
    DatabaseServer databaseServer;

    Dialect dialect;

    DmlQueryBuilder dmlQueryBuilder;

    DdlQueryBuilder ddlQueryBuilder;

    JdbcTemplate jdbcTemplate;

    EntityPersister entityPersister;

    EntityManager entityManager;

    @BeforeEach
    void setup() throws SQLException {
        databaseServer = new H2();
        dialect = DialectFactory.create(databaseServer.getClass());
        dmlQueryBuilder = new DmlQueryBuilder(dialect);
        ddlQueryBuilder = new DdlQueryBuilder(dialect);
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        entityPersister = new EntityPersisterImpl(jdbcTemplate, dmlQueryBuilder);

        entityManager = new EntityManagerImpl(entityPersister, dmlQueryBuilder, jdbcTemplate, persistenceContext);

        jdbcTemplate.execute(ddlQueryBuilder.buildCreateTableQuery(PersonWithTransientAnnotation.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlQueryBuilder.buildDropTableQuery(PersonWithTransientAnnotation.class));
        databaseServer.stop();
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {
        @Test
        @DisplayName("Long 타입 id에 해당하는 엔티티를 구한다.")
        void succeedToFindById() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );
            jdbcTemplate.execute(dmlQueryBuilder.buildInsertQuery(person));

            // when
            PersonWithTransientAnnotation personFound = entityManager.findById(PersonWithTransientAnnotation.class, 1L);

            // then
            assertEquals(1L, personFound.getId());
        }

        @Test
        @DisplayName("해당하는 엔티티가 없다면 에러를 내뱉는다.")
        void failToFindById() {
            assertThrows(RuntimeException.class, () -> {
                entityManager.findById(PersonWithTransientAnnotation.class, 1L);
            });
        }
    }

    @Nested
    @DisplayName("persist 테스트")
    class PersistTest {
        @Test
        @DisplayName("주어진 엔티티를 디비에 저장한다.")
        void succeedToPersist() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );
            entityManager.persist(person);

            // when
            PersonWithTransientAnnotation foundPerson = entityManager.findById(
                    PersonWithTransientAnnotation.class,
                    1L
            );

            // then
            assertEquals(foundPerson.getName(), person.getName());
        }
    }

    @Nested
    @DisplayName("remove 테스트")
    class RemoveTest {
        @Test
        @DisplayName("주어진 엔티티를 디비에서 제거한다.")
        void succeedToRemove() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );
            entityManager.persist(person);

            // when
            entityManager.remove(person);

            // then
            assertThrows(RuntimeException.class, () -> {
                entityManager.findById(PersonWithTransientAnnotation.class, 1L);
            });
        }

        @Test
        @DisplayName("PK가 없는 객체를 제거하려 하면 에러가 발생한다.")
        void failToRemove() {
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    "홍길동", 20, "test@test.com", 1
            );

            assertThrows(ColumnInvalidException.class, () -> {
                entityManager.remove(person);
            });
        }
    }

    @Nested
    @DisplayName("merge 테스트")
    class MergeTest {
        @Test
        @DisplayName("저장된 엔티티라면 디비에서 업데이트한다.")
        void succeedToUpdate() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );
            entityManager.persist(person);

            // when
            person.setAge(30);
            entityManager.merge(person);

            // then
            PersonWithTransientAnnotation foundPerson = entityManager.findById(PersonWithTransientAnnotation.class, 1L);
            assertEquals(30, foundPerson.getAge());
        }

        @Test
        @DisplayName("저장된 엔티티가 아니라면 새로 저장한다.")
        void succeedToAddNew() {
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );

            entityManager.merge(person);

            assertNotNull(entityManager.findById(PersonWithTransientAnnotation.class, 1L));
        }
    }
}
