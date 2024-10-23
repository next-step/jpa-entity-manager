package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonWithTransientAnnotation;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dialect.type.H2DataTypeRegistry;
import persistence.sql.dml.DmlQueryBuilder;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityPersisterTest {
    private DatabaseServer databaseServer;

    private EntityPersister entityPersister;

    private JdbcTemplate jdbcTemplate;

    private DmlQueryBuilder dmlQueryBuilder;

    private DdlQueryBuilder ddlQueryBuilder;

    private String SELECT_QUERY;

    private PersonWithTransientAnnotation FIXTURE;

    @BeforeEach
    void setup() throws SQLException {
        databaseServer = new H2();
        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        Dialect dialect = new H2Dialect(new H2DataTypeRegistry());
        dmlQueryBuilder = new DmlQueryBuilder(dialect);
        ddlQueryBuilder = new DdlQueryBuilder(dialect);

        entityPersister = new EntityPersisterImpl(jdbcTemplate, dmlQueryBuilder);

        SELECT_QUERY = dmlQueryBuilder.buildSelectByIdQuery(PersonWithTransientAnnotation.class, 1L);
        FIXTURE = new PersonWithTransientAnnotation(
                1L, "홍길동", 20, "test@test.com", 1
        );

        jdbcTemplate.execute(ddlQueryBuilder.buildCreateTableQuery(PersonWithTransientAnnotation.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlQueryBuilder.buildDropTableQuery(PersonWithTransientAnnotation.class));
        databaseServer.stop();
    }

    @Test
    @DisplayName("객체를 디비에 UPDATE한다.")
    void testUpdate() {
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

    @Test
    @DisplayName("객체를 디비에서 DELETE한다.")
    void testDelete() {
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
}
