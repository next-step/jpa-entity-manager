package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
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

import static org.junit.jupiter.api.Assertions.*;

public class EntityLoaderTest {
    private static DatabaseServer databaseServer;
    private static JdbcTemplate jdbcTemplate;
    private static final Dialect dialect = new H2Dialect(new H2DataTypeRegistry());
    private static final DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(dialect);
    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder(dialect);

    @BeforeEach
    void setup() throws SQLException {
        databaseServer = new H2();
        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());

        jdbcTemplate.execute(ddlQueryBuilder.buildCreateTableQuery(PersonWithTransientAnnotation.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlQueryBuilder.buildDropTableQuery(PersonWithTransientAnnotation.class));
        databaseServer.stop();
    }

    @Test
    @DisplayName("객체를 데이터베이스에서 찾아오는 데 성공한다.")
    void testFind() {
        // given
        PersonWithTransientAnnotation insertingUser = new PersonWithTransientAnnotation(
                1L, "홍길동", 20, "test@test.com", 1
        );
        EntityPersister entityPersister = new EntityPersisterImpl(jdbcTemplate, dmlQueryBuilder);
        entityPersister.insert(insertingUser);

        // when
        EntityLoader entityLoader = new EntityLoaderImpl(jdbcTemplate, dmlQueryBuilder);
        PersonWithTransientAnnotation foundUser = entityLoader.find(PersonWithTransientAnnotation.class, 1L);

        // then
        assertEquals(insertingUser.getId(), foundUser.getId());
    }
}
