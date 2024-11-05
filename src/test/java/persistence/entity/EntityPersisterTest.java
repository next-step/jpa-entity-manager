package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import org.junit.jupiter.api.*;
import persistence.fixture.PersonWithTransientAnnotation;
import persistence.model.exception.ColumnInvalidException;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dialect.type.H2DataTypeRegistry;
import persistence.sql.dml.DmlQueryBuilder;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntityPersisterTest {
    private DatabaseServer databaseServer;
    private EntityPersister entityPersister;
    private JdbcTemplate jdbcTemplate;
    private DdlQueryBuilder ddlQueryBuilder;
    private String SELECT_QUERY;
    private PersonWithTransientAnnotation FIXTURE;

    @BeforeEach
    void setup() throws SQLException {
        databaseServer = new H2();
        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());

        Dialect dialect = new H2Dialect(new H2DataTypeRegistry());
        DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder(dialect);

        entityPersister = new EntityPersisterImpl(jdbcTemplate, dmlQueryBuilder);

        SELECT_QUERY = dmlQueryBuilder.buildSelectByIdQuery("users", new AbstractMap.SimpleEntry<>("id", 1L));
        FIXTURE = new PersonWithTransientAnnotation(
                1L, "홍길동", 20, "test@test.com", 1
        );

        ddlQueryBuilder = new DdlQueryBuilder(dialect);
        jdbcTemplate.execute(ddlQueryBuilder.buildCreateTableQuery(PersonWithTransientAnnotation.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlQueryBuilder.buildDropTableQuery(PersonWithTransientAnnotation.class));
        databaseServer.stop();
    }

    @Nested
    class UpdateTest {
        @Test
        @DisplayName("객체를 디비에 UPDATE한다.")
        void succeedToUpdate() {
            // given
            entityPersister.insert(FIXTURE);

            // when
            FIXTURE.setName("홍길동2");
            entityPersister.update(FIXTURE);

            // then
            PersonWithTransientAnnotation updatedPerson = jdbcTemplate.queryForObject(SELECT_QUERY, resultSet ->
                    new RowMapperImpl<>(PersonWithTransientAnnotation.class).mapRow(resultSet)
            );

            assertEquals("홍길동2", updatedPerson.getName());
        }

        @Test
        @DisplayName("PK 값이 없으면 UPATE에 실패한다.")
        void failToUpdate() {
            FIXTURE = new PersonWithTransientAnnotation(
                    "홍길동", 20, "test@test.com", 1
            );

            assertThrows(ColumnInvalidException.class, () -> {
                entityPersister.update(FIXTURE);
            });
        }
    }

    @Test
    @DisplayName("객체를 디비에 INSERT한다.")
    void testInsert() {
        List<PersonWithTransientAnnotation> beforeInsertSelectResult = jdbcTemplate.query(SELECT_QUERY, resultSet ->
                new RowMapperImpl<>(PersonWithTransientAnnotation.class).mapRow(resultSet)
        );

        // when
        entityPersister.insert(FIXTURE);

        // then
        List<PersonWithTransientAnnotation> afterInsertSelectResult = jdbcTemplate.query(SELECT_QUERY, resultSet ->
                new RowMapperImpl<>(PersonWithTransientAnnotation.class).mapRow(resultSet)
        );
        assertAll(
                () -> assertEquals(0, beforeInsertSelectResult.size()),
                () -> assertEquals(1, afterInsertSelectResult.size())
        );
    }

    @Nested
    class DeleteTest {
        @Test
        @DisplayName("객체를 디비에서 DELETE한다.")
        void succeedToDelete() {
            // given
            entityPersister.insert(FIXTURE);

            List<PersonWithTransientAnnotation> beforeDeleteSelectResult = jdbcTemplate.query(SELECT_QUERY, resultSet ->
                    new RowMapperImpl<>(PersonWithTransientAnnotation.class).mapRow(resultSet)
            );

            // when
            entityPersister.delete(FIXTURE);

            // then
            List<PersonWithTransientAnnotation> afterDeleteSelectResult = jdbcTemplate.query(SELECT_QUERY, resultSet ->
                    new RowMapperImpl<>(PersonWithTransientAnnotation.class).mapRow(resultSet)
            );
            assertAll(
                    () -> assertEquals(1, beforeDeleteSelectResult.size()),
                    () -> assertEquals(0, afterDeleteSelectResult.size())
            );
        }

        @Test
        @DisplayName("객체에 PK 값이 없다면 삭제에 실패한다.")
        void failToDelete() {
            FIXTURE = new PersonWithTransientAnnotation(
                    "홍길동", 20, "test@test.com", 1
            );

            assertThrows(ColumnInvalidException.class, () -> {
                entityPersister.delete(FIXTURE);
            });
        }
    }
}
