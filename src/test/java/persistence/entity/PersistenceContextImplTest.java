package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import domain.SelectPerson;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.ddl.DmlQuery;
import persistence.sql.dml.Query;

import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;

class PersistenceContextImplTest {

    private PersistenceContextImpl persistenceContext;
    private EntityPersister<SelectPerson> persister;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() throws SQLException {
        DatabaseServer server = new H2();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        persistenceContext = new PersistenceContextImpl();
        persister = new EntityPersister<>(jdbcTemplate, SelectPerson.class, Query.getInstance());

        테이블을_생성함(SelectPerson.class);
    }

    @Nested
    @DisplayName("영속성 컨텍스트에서 데이터를 저장합니다.")
    class addEntity {

        @Test
        @DisplayName("객체가 null일때 처리되지 않도록 처리")
        void entityIsNull() {
            //given
            final Long id = 9898L;
            SelectPerson person = null;

            final Integer hashCode = id.hashCode();

            //when
            persistenceContext.addEntity(hashCode, id, person);

            Object result = persistenceContext.getEntity(hashCode, persister, id);

            //then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("성공적으로 데이터를 저장합니다.")
        void success() throws SQLException {
            //given
            final Long id = 9898L;
            final String name = "name";
            final Integer age = 30;
            final String email = "email";
            final int index = 3;

            final Integer hashCode = id.hashCode();

            SelectPerson person = new SelectPerson(id, name, age, email, index);

            //when
            영속성_컨텍스트에서_데이터를_저장한다(hashCode, id, person);
            persistenceContext.flush(Map.of("domain.SelectPerson", persister));

            SelectPerson result = (SelectPerson) 영속성_컨텍스트에서_데이터를_가져온다(hashCode, id);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result.getId()).isEqualTo(id);
                softAssertions.assertThat(result.getName()).isEqualTo(name);
                softAssertions.assertThat(result.getAge()).isEqualTo(age);
                softAssertions.assertThat(result.getEmail()).isEqualTo(email);
                softAssertions.assertThat(result.getIndex()).isNull();
            });
        }

        @Test
        @DisplayName("서로 다른 객체가 영속성 컨텍스트에 저장이 된다")
        void differentEntitySave() throws SQLException {
            테이블을_생성함(Person.class);
            //given
            final Long id = 333L;
            SelectPerson selectPerson = new SelectPerson(id, "name", 30, "email", 3);
            Person person = new Person(id, "name", 30, "email", 3);

            final int selectPersonHashCode = selectPerson.hashCode();
            final int personHashCode = person.hashCode();

            //when
            영속성_컨텍스트에서_데이터를_저장한다(selectPersonHashCode, id, selectPerson);
            영속성_컨텍스트에서_데이터를_저장한다(personHashCode, id, person);
            persistenceContext.flush(
                    Map.of("domain.SelectPerson", persister, "domain.Person", new EntityPersister<>(jdbcTemplate, Person.class, Query.getInstance()))
            );

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(영속성_컨텍스트에서_데이터를_가져온다(selectPersonHashCode, id)).isNotNull();
                softAssertions.assertThat(영속성_컨텍스트에서_데이터를_가져온다(personHashCode, id)).isNotNull();
            });

            테이블을_삭제함(Person.class);
        }
    }

    @Nested
    @DisplayName("영속성 컨텍스트에서 데이터를 가져옵니다.")
    class getEntity {

        @Test
        @DisplayName("성공적으로 데이터를 가져옵니다.")
        void success() throws SQLException {
            //given
            final Long id = 3333L;
            final String name = "name";
            final Integer age = 30;
            final String email = "email";
            final int index = 3;

            final Integer hashCode = id.hashCode();

            SelectPerson person = new SelectPerson(id, name, age, email, index);
            영속성_컨텍스트에서_데이터를_저장한다(hashCode, id, person);
            persistenceContext.flush(Map.of("domain.SelectPerson", persister));

            //when
            SelectPerson result = persistenceContext.getEntity(hashCode, persister, id);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result.getId()).isEqualTo(id);
                softAssertions.assertThat(result.getName()).isEqualTo(name);
                softAssertions.assertThat(result.getAge()).isEqualTo(age);
                softAssertions.assertThat(result.getEmail()).isEqualTo(email);
                softAssertions.assertThat(result.getIndex()).isNull();
            });
        }

        @Test
        @DisplayName("영속성 컨텍스트에 저장되지 않은 값을 가져오면 null 반환")
        void returnNull() {
            //given
            final Integer hashCode = -93939393;

            //when
            SelectPerson result = persistenceContext.getEntity(hashCode, persister, hashCode);

            //then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("동일성에 관련한 테스트 코드 작성하기")
        void equals() {
            //given
            final Long id = 2233L;
            SelectPerson selectPerson = new SelectPerson(id, "name", 30, "email", 3);
            final Integer hashcode = selectPerson.hashCode();
            persistenceContext.addEntity(hashcode, id, selectPerson);
            persistenceContext.flush(Map.of("domain.SelectPerson", persister));

            //when
            SelectPerson firstResult = (SelectPerson) 영속성_컨텍스트에서_데이터를_가져온다(selectPerson.hashCode(), id);
            SelectPerson secondResult = (SelectPerson) 영속성_컨텍스트에서_데이터를_가져온다(selectPerson.hashCode(), id);

            //then
            assertThat(firstResult).isEqualTo(secondResult);
        }
    }

    @Nested
    @DisplayName("영속성 컨텍스트에서 데이터를 삭제합니다.")
    class removeEntity {

        @Test
        @DisplayName("성공적으로 데이터를 삭제합니다.")
        void success() throws SQLException {
            //given
            final Long id = 5555L;
            final Integer hashCode = id.hashCode();

            SelectPerson person = new SelectPerson(id, "name", 30, "email", 3);
            영속성_컨텍스트에서_데이터를_저장한다(hashCode, id, person);

            //when
            persistenceContext.removeEntity(hashCode);

            //then
            assertThat(persistenceContext.getEntity(hashCode, persister, id)).isNull();
        }
    }

    @AfterEach
    void after() {
        테이블을_삭제함(SelectPerson.class);
    }

    private void 영속성_컨텍스트에서_데이터를_저장한다(Integer hashCode, Object id, Object entity) throws SQLException {
        EntityPersister<SelectPerson> entityPersister;

        DatabaseServer server = new H2();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityPersister = new EntityPersister<>(jdbcTemplate, SelectPerson.class, Query.getInstance());

        persistenceContext.addEntity(hashCode, id, entityPersister.getEntity(entity));
    }

    private Object 영속성_컨텍스트에서_데이터를_가져온다(Integer hashcode, Long id) {
        return persistenceContext.getEntity(hashcode, persister, id);
    }

    private <T> void 테이블을_생성함(Class<T> tClass) {
        final TableName tableName = TableName을_생성함(tClass);
        final Columns columns = Columns을_생성함(tClass);

        jdbcTemplate.execute(DmlQuery.getInstance().create(tableName, columns));
    }

    private <T> void 테이블을_삭제함(Class<T> tClass) {
        jdbcTemplate.execute(DmlQuery.getInstance().drop(TableName을_생성함(tClass)));
    }
}
