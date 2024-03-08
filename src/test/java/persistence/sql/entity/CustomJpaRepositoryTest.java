package persistence.sql.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DdlCreateQueryBuilder;
import persistence.sql.ddl.DdlDropQueryBuilder;
import persistence.sql.dialect.h2.H2Dialect;
import persistence.sql.dml.domain.Person;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomJpaRepositoryTest {

    private EntityManagerImpl entityManager;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() throws SQLException {
        final DatabaseServer databaseServer = new H2();
        databaseServer.start();
        final Connection connection = databaseServer.getConnection();
        jdbcTemplate = new JdbcTemplate(connection);

        final DdlDropQueryBuilder ddlDropQueryBuilder = new DdlDropQueryBuilder();
        final String dropSql = ddlDropQueryBuilder.dropDdl(Person.class);
        jdbcTemplate.execute(dropSql);

        final DdlCreateQueryBuilder ddlCreateQueryBuilder = new DdlCreateQueryBuilder(new H2Dialect());
        final String createSql = ddlCreateQueryBuilder.createDdl(Person.class);
        jdbcTemplate.execute(createSql);

        EntityPersisterImpl entityPersister = new EntityPersisterImpl(jdbcTemplate);
        final EntityLoaderImpl entityLoader = new EntityLoaderImpl(jdbcTemplate);
        entityManager = new EntityManagerImpl(entityPersister, entityLoader);

    }
    @Test
    @DisplayName("save 시 dirty checking 로직 구현")
    void saveWithDirty() {
        final Person person = (Person) entityManager.persist(new Person( 1L, "simpson", 31, "simpson@naver.com"));

        assertThat(entityManager.isDirty(person)).isFalse();

        person.changeAge(25);

        assertThat(entityManager.isDirty(person)).isTrue();
    }
}
