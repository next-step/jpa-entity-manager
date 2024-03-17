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
import persistence.sql.entity.impl.EntityLoaderImpl;
import persistence.sql.entity.impl.EntityManagerImpl;
import persistence.sql.entity.impl.EntityPersisterImpl;
import persistence.sql.entity.impl.PersistenceContextImpl;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

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
        final EntityMetaCreator entityMetaCreator = new SimpleEntityMetaCreator();
        final Table table = entityMetaCreator.createByClass(Person.class);

        final DdlDropQueryBuilder ddlDropQueryBuilder = new DdlDropQueryBuilder();
        final String dropSql = ddlDropQueryBuilder.dropDdl(table);
        jdbcTemplate.execute(dropSql);

        final DdlCreateQueryBuilder ddlCreateQueryBuilder = new DdlCreateQueryBuilder(new H2Dialect());
        final String createSql = ddlCreateQueryBuilder.createDdl(table);
        jdbcTemplate.execute(createSql);

        EntityPersisterImpl entityPersister = new EntityPersisterImpl(jdbcTemplate, entityMetaCreator);
        final EntityLoaderImpl entityLoader = new EntityLoaderImpl(jdbcTemplate, entityMetaCreator);
        entityManager = new EntityManagerImpl(entityPersister, entityLoader, new PersistenceContextImpl());

    }
    @Test
    @DisplayName("save 시 dirty checking 로직 구현")
    void saveWithDirty() throws IllegalAccessException {
        final Person person = (Person) entityManager.persist(new Person( "simpson", 31, "simpson@naver.com"));

        assertThat(entityManager.isDirty(person)).isFalse();

        person.changeAge(25);

        assertThat(entityManager.isDirty(person)).isTrue();
    }
}
