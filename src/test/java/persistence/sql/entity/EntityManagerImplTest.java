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
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.domain.Person;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntityManagerImplTest {
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() throws SQLException {
        final DatabaseServer databaseServer = new H2();
        databaseServer.start();
        final Connection connection = databaseServer.getConnection();
        jdbcTemplate = new JdbcTemplate(connection);

        final DdlDropQueryBuilder ddlDropQueryBuilder = new DdlDropQueryBuilder(Person.class, new H2Dialect());
        final String dropSql = ddlDropQueryBuilder.dropDdl();
        jdbcTemplate.execute(dropSql);

        final DdlCreateQueryBuilder ddlCreateQueryBuilder = new DdlCreateQueryBuilder(Person.class, new H2Dialect());
        final String createSql = ddlCreateQueryBuilder.createDdl();
        jdbcTemplate.execute(createSql);
    }

    @DisplayName("EntityManagerImpl find를 호출하면 엔티티가 리턴된다.")
    @Test
    void findTest() {
        final Person person = new Person( 1L, "simpson", 31, "simpson@naver.com");
        final InsertQueryBuilder queryBuilder = new InsertQueryBuilder(Person.class, new H2Dialect());
        jdbcTemplate.execute(queryBuilder.createInsertQuery(person));

        final EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);
        final Person findPerson = entityManager.find(person.getClass(), 1L);

        assertThat(person).isEqualTo(findPerson);
    }

    @DisplayName("EntityManagerImpl persist를 호출하면 엔티티를 저장한다.")
    @Test
    void persistTest() {
        final Person person = new Person( 1L, "simpson", 31, "simpson@naver.com");
        final EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);

        entityManager.persist(person);

        final Person savedPerson = entityManager.find(person.getClass(), 1L);
        assertThat(savedPerson).isEqualTo(person);
    }

    @DisplayName("EntityManagerImpl delete를 호출하면 엔티티를 삭제한다.")
    @Test
    void removeTest() {
        final Person person = new Person( 1L, "simpson", 31, "simpson@naver.com");
        final InsertQueryBuilder queryBuilder = new InsertQueryBuilder(Person.class, new H2Dialect());
        jdbcTemplate.execute(queryBuilder.createInsertQuery(person));
        final EntityManager entityManager = new EntityManagerImpl(jdbcTemplate);

        entityManager.remove(person);

        assertThatThrownBy(() -> entityManager.find(person.getClass(), 1L))
                .isInstanceOf(RuntimeException.class);
    }
}