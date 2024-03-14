package persistence.entity;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub(0L);

        // when
        entityManager.persist(person);

        // then
        final String select = "select * from users";
        final List<PersonV3> result = jdbcTemplate.query(select, rowMapper);
        assertThat(result).hasSize(1)
                .extracting("name", "age", "email", "index")
                .contains(tuple(person.getName(), person.getAge(), person.getEmail(), null));
    }

    @DisplayName("영속성 컨텍스트에서 관리중인 엔티티를 persist 하면 예외를 반환한다")
    @Test
    public void persistManagedEntity() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub(0L);
        entityManager.persist(person);

        // when then
        assertThatThrownBy(() -> entityManager.persist(person))
                .isInstanceOf(EntityExistsException.class);
    }

    @DisplayName("더티 체킹 후 변한게 존재하면 업데이트를 한다")
    @Test
    public void mergeWithDirtyChecking() throws Exception {
        // given
        final Class<PersonV3> clazz = PersonV3.class;
        final long id = 1L;
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub(id);
        entityManager.persist(person);

        final String updateName = "update name";
        person.setName(updateName);

        // when
        entityManager.merge(person);

        // then
        final String select = "select * from users";
        final List<PersonV3> result = jdbcTemplate.query(select, rowMapper);
        assertThat(result).hasSize(1)
                .extracting("name", "age", "email", "index")
                .contains(tuple(updateName, person.getAge(), person.getEmail(), null));
    }

    @DisplayName("변경된 값이 없는 엔티티는 업데이트 되지 않는다")
    @Test
    public void mergeNotChangeEntity() throws Exception {
        // given
        final Class<PersonV3> clazz = PersonV3.class;
        final long id = 1L;
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub(id);
        entityManager.persist(person);

        person.setIndex(100);

        // when
        entityManager.merge(person);

        // then
        final String select = "select * from users";
        final List<PersonV3> result = jdbcTemplate.query(select, rowMapper);
        assertThat(result).hasSize(1)
                .extracting("name", "age", "email", "index")
                .contains(tuple(person.getName(), person.getAge(), person.getEmail(), null));
    }

    @DisplayName("엔티티 객체로 엔티티를 db 에서 삭제한다")
    @Test
    public void remove() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        entityManager.persist(person);

        // when
        entityManager.remove(person);

        // then
        final String select = "select * from users";
        final List<PersonV3> result = jdbcTemplate.query(select, rowMapper);
        assertThat(result).isEmpty();
    }

    @DisplayName("영속성 컨텍스트에서 관리되고 있지 않은 엔티티를 remove 하면 예외를 반환한다")
    @Test
    public void removeGoneEntity() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final String insertQuery = generateUserTableStubInsertQuery(person);
        jdbcTemplate.execute(insertQuery);

        // when then
        assertThatThrownBy(() -> entityManager.remove(person))
                .isInstanceOf(EntityNotFoundException.class);
    }

}
