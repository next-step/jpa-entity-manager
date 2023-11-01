package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import fixture.PersonFixtureFactory;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.DdlQueryGenerator;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.meta.EntityMeta;
import persistence.sql.meta.MetaFactory;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPersistenceContextTest {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private DdlQueryGenerator ddlQueryGenerator;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());

        EntityMeta personMeta = MetaFactory.get(Person.class);
        DialectFactory dialectFactory = DialectFactory.getInstance();
        ddlQueryGenerator = DdlQueryGenerator.of(dialectFactory.getDialect(jdbcTemplate.getDbmsName()));
        jdbcTemplate.execute(ddlQueryGenerator.generateCreateQuery(personMeta));
    }

    @AfterEach
    void tearDown() {
        EntityMeta personMeta = MetaFactory.get(Person.class);
        jdbcTemplate.execute(ddlQueryGenerator.generateDropQuery(personMeta));
        server.stop();
    }

    @Test
    @DisplayName("저장된 엔티티를 조회하여 인스턴스 존재여부를 확인한다")
    void getEntity() {
        DefaultPersistenceContext persistenceContext = DefaultPersistenceContext.of(jdbcTemplate);
        PersonFixtureFactory.getFixtures()
                .forEach(persistenceContext::addEntity);

        Person person = persistenceContext.getEntity(Person.class, 1L);
        assertThat(person).isNotNull();
    }

    @Test
    @DisplayName("엔티티를 생성해 영속화한 후 생성한 엔티티를 재조회하여 존재여부를 확인한다")
    void addEntity() {
        DefaultPersistenceContext persistenceContext = DefaultPersistenceContext.of(jdbcTemplate);
        Person persistTarget = new Person("테스트", 20, "test@domain.com", 1);
        persistenceContext.addEntity(persistTarget);

        Person person = persistenceContext.getEntity(Person.class, 1L);
        assertThat(person).isNotNull();
        assertThat(person).isEqualTo(persistTarget);
    }

    @Test
    @DisplayName("엔티티를 삭제한 후, 삭제한 엔티티의 식별자로 엔티티를 조회하여 인스턴스가 존재하지 않음을 확인한다.")
    void removeEntity() {
        DefaultPersistenceContext persistenceContext = DefaultPersistenceContext.of(jdbcTemplate);
        persistenceContext.addEntity(new Person("테스트", 20, "test@domain.com", 1));

        Person person = persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.removeEntity(person);

        person = persistenceContext.getEntity(Person.class, 1L);
        assertThat(person).isNull();
    }
}
