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
    private static EntityPersister entityPersister;
    private static EntityLoader entityLoader;
    private static final Dialect dialect = new H2Dialect(new H2DataTypeRegistry());
    private static final DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(dialect);
    private static final DmlQueryBuilder dmlQueryBuilder = new DmlQueryBuilder(dialect);

    @BeforeEach
    void setup() throws SQLException {
        databaseServer = new H2();
        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        entityPersister =  new EntityPersisterImpl(jdbcTemplate, dmlQueryBuilder);
        entityLoader = new EntityLoaderImpl(jdbcTemplate, dmlQueryBuilder);

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
        entityPersister.insert(insertingUser);

        // when
        PersonWithTransientAnnotation foundUser = entityLoader.find(PersonWithTransientAnnotation.class, 1L);

        // then
        assertEquals(insertingUser.getId(), foundUser.getId());
    }

    @Test
    @DisplayName("객체가 데이터베이스에 있는지 확인한다.")
    void testExists() {
        // given
        PersonWithTransientAnnotation insertingUser = new PersonWithTransientAnnotation(
                1L, "홍길동", 20, "test@test.com", 1
        );
        entityPersister.insert(insertingUser);

        // when
        boolean exists = entityLoader.exists(PersonWithTransientAnnotation.class, 1L);

        // then
        assertTrue(exists);
    }
}
