package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
import persistence.model.EntityPrimaryKey;
import persistence.model.exception.ColumnInvalidException;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dialect.type.H2DataTypeRegistry;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.fixture.PersonWithTransientAnnotation;
import persistence.util.ReflectionUtil;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EntityManagerTest {
    private static DatabaseServer databaseServer;
    private static JdbcTemplate jdbcTemplate;
    private static final Dialect dialect = new H2Dialect(new H2DataTypeRegistry());
    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder(dialect);
    private static final DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(dialect);
    private static PersistenceContext persistenceContext;
    private static EntityPersister entityPersister;
    private static EntityLoader entityLoader;
    private static EntityManager entityManager;

    @BeforeEach
    void setup() throws SQLException {
        databaseServer = new H2();
        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());

        persistenceContext = new PersistenceContextImpl();
        entityPersister = new EntityPersisterImpl(jdbcTemplate, dmlQueryBuilder);
        entityLoader = new EntityLoaderImpl(jdbcTemplate, dmlQueryBuilder);

        entityManager = new EntityManagerImpl(entityPersister, entityLoader, persistenceContext);

        jdbcTemplate.execute(ddlQueryBuilder.buildCreateTableQuery(PersonWithTransientAnnotation.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlQueryBuilder.buildDropTableQuery(PersonWithTransientAnnotation.class));
        databaseServer.stop();
    }

    @Nested
    @DisplayName("find 테스트")
    class FindTest {
        @Test
        @DisplayName("Long 타입 id에 해당하는 엔티티를 구한다.")
        void succeedToFindById() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );

            List<Map.Entry<String, Object>> updatingColumns = new ArrayList<>();
            updatingColumns.add(new AbstractMap.SimpleEntry<>("id", 1L));
            updatingColumns.add(new AbstractMap.SimpleEntry<>("nick_name", "홍길동2"));
            updatingColumns.add(new AbstractMap.SimpleEntry<>("old", 30));
            updatingColumns.add(new AbstractMap.SimpleEntry<>("email", "test@test.com"));

            jdbcTemplate.execute(dmlQueryBuilder.buildInsertQuery("users", updatingColumns));

            // when
            PersonWithTransientAnnotation personFound = entityManager.find(PersonWithTransientAnnotation.class, 1L);

            // then
            assertEquals(1L, personFound.getId());
        }

        @Test
        @DisplayName("해당하는 엔티티가 없다면 null을 반환한다.")
        void failToFindById() {
            assertNull(entityManager.find(PersonWithTransientAnnotation.class, 1L));
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

            // when
            entityManager.persist(person);

            // then
            PersonWithTransientAnnotation contextFound = persistenceContext.getEntity(
                    PersonWithTransientAnnotation.class, 1L
            );
            PersonWithTransientAnnotation databaseFound = entityLoader.find(
                    PersonWithTransientAnnotation.class, 1L
            );
            assertAll(
                    () -> assertSame(contextFound, person),
                    () -> assertEquals(databaseFound.getName(), person.getName())
            );
        }

        @Test
        @DisplayName("영속성 컨텍스트에 이미 존재하는 엔티티라면 에러를 뱉는다.")
        void failToPersistForAlreadyExistingEntity() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );
            entityManager.persist(person);

            // when, then
            assertThrows(EntityExistsException.class, () -> {
                entityManager.persist(person);
            });
        }

        @Test
        @DisplayName("영속성 컨텍스트에 없더라도 데이터베이스에 있다면 에러를 뱉는다.")
        void failToPersistForAlreadyExistingDatabase() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );
            entityPersister.insert(person);

            // when, then
            assertThrows(EntityExistsException.class, () -> {
                entityManager.persist(person);
            });
        }

        @Test
        @DisplayName("Id가 없는 엔티티는, 데이터베이스에 생성 후 생성된 Id를 영속성 컨텍스트에 저장한다.")
        void succeedToPersistEntityWithoutId() {
            // given
            PersonWithTransientAnnotation person = new PersonWithTransientAnnotation("test@test.com");

            // when
            entityManager.persist(person);

            // then
            EntityPrimaryKey pk = EntityPrimaryKey.build(person);
            Object foundEntity = persistenceContext.getEntity(PersonWithTransientAnnotation.class, pk.keyValue());
            Object foundEntityId = ReflectionUtil.getFieldNameAndValue(foundEntity, Id.class).getValue();

            assertEquals(1L, foundEntityId);
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
            PersonWithTransientAnnotation contextFound = persistenceContext.getEntity(
                    PersonWithTransientAnnotation.class, 1L
            );
            boolean isFoundInDatabase = entityLoader.exists(
                    PersonWithTransientAnnotation.class, 1L
            );

            assertAll(
                    () -> assertNull(contextFound),
                    () -> assertFalse(isFoundInDatabase)
            );

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
            PersonWithTransientAnnotation contextFound = persistenceContext.getEntity(
                    PersonWithTransientAnnotation.class, 1L
            );
            PersonWithTransientAnnotation databaseFound = entityLoader.find(
                    PersonWithTransientAnnotation.class, 1L
            );

            assertAll(
                    () -> assertEquals(30, contextFound.getAge()),
                    () -> assertEquals(30, databaseFound.getAge())
            );
        }

        @Test
        @DisplayName("저장된 엔티티가 아니라면 새로 저장한다.")
        void succeedToAddNew() {
            PersonWithTransientAnnotation entity = new PersonWithTransientAnnotation(
                    1L, "홍길동", 20, "test@test.com", 1
            );

            PersonWithTransientAnnotation mergeResult = entityManager.merge(entity);
            PersonWithTransientAnnotation contextFound = persistenceContext.getEntity(
                    PersonWithTransientAnnotation.class, 1L
            );
            PersonWithTransientAnnotation databaseFound = entityLoader.find(
                    PersonWithTransientAnnotation.class, 1L
            );

            assertAll(
                    () -> assertSame(contextFound, mergeResult),
                    () -> assertEquals(databaseFound.getId(), mergeResult.getId())
            );

        }
    }
}
