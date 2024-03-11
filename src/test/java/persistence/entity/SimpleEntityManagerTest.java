package persistence.entity;

import jdbc.RowMapper;
import org.junit.jupiter.api.*;
import persistence.JdbcServerDmlQueryTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SingleEntityLoader;
import persistence.entity.persister.SingleTableEntityPersister;
import persistence.model.MappingMetaModel;
import persistence.sql.ddl.PersonV3;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DefaultDmlQueryBuilder;
import persistence.sql.mapping.TableBinder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SimpleEntityManagerTest extends JdbcServerDmlQueryTestSupport {

    private final TableBinder tableBinder = new TableBinder();
    private final Dialect dialect = new H2Dialect();
    private final DefaultDmlQueryBuilder dmlQueryBuilder = new DefaultDmlQueryBuilder(dialect);
    private final Class<PersonV3> personV3Class = PersonV3.class;
    private final SingleTableEntityPersister personEntityPersister = new SingleTableEntityPersister(personV3Class.getName(), tableBinder, dmlQueryBuilder, jdbcTemplate, personV3Class);
    private final EntityLoader entityLoader = new SingleEntityLoader(tableBinder, dmlQueryBuilder, jdbcTemplate);
    private final MappingMetaModel mappingMetaModel = new MappingMetaModel(personEntityPersister);
    private final EntityManager entityManager = new SimpleEntityManager(mappingMetaModel, entityLoader);
    private final RowMapper<PersonV3> rowMapper = new EntityRowMapper<>(personV3Class);

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("delete from users");
    }

    @DisplayName("엔티티 클래스 타입과 id 값으로 엔티티를 조회 후 반환한다")
    @Test
    @Order(0)
    public void find() throws Exception {
        // given
        final Class<PersonV3> clazz = PersonV3.class;
        final long id = 1L;
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub(id);
        final String insertQuery = generateUserTableStubInsertQuery(person);
        jdbcTemplate.execute(insertQuery);

        // when
        final PersonV3 entity = entityManager.find(clazz, id);

        // then
        assertThat(entity).isNotNull()
                .extracting("id", "name", "age", "email", "index")
                .contains(person.getId(), person.getName(), person.getAge(), person.getEmail(), null);
    }

    @DisplayName("엔티티 객체로 엔티티를 insert 한다")
    @Test
    public void persist() throws Exception {
        // given
        final String name = "name";
        final int age = 20;
        final String email = "email@domain.com";
        final PersonV3 person = new PersonV3(0L, name, age, email, 1);

        // when
        entityManager.persist(person);

        // then
        final String select = "select * from users";
        final List<PersonV3> result = jdbcTemplate.query(select, rowMapper);
        assertThat(result).hasSize(1)
                .extracting("name", "age", "email", "index")
                .contains(tuple(name, age, email, null));
    }

    @DisplayName("엔티티 객체로 엔티티를 db 에서 삭제한다")
    @Test
    public void remove() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final String insertQuery = generateUserTableStubInsertQuery(person);
        jdbcTemplate.execute(insertQuery);

        // when
        entityManager.remove(person);

        // then
        final String select = "select * from users";
        final List<PersonV3> result = jdbcTemplate.query(select, rowMapper);
        assertThat(result).isEmpty();
    }

}
