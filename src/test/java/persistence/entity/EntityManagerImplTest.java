package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import domain.SelectPerson;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.ddl.DmlQuery;

class EntityManagerImplTest {

    private static final Class<SelectPerson> clazz = SelectPerson.class;

    private static DatabaseServer server;
    private static DmlQuery dmlQuery;

    private static EntityManager entityManager;

    private static JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeInit() throws SQLException {
        server = new H2();
        server.start();

        dmlQuery = DmlQuery.getInstance();

        entityManager = EntityManagerFactory.of(server.getConnection());
        jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @BeforeEach
    void init() throws SQLException {
        entityManager = EntityManagerFactory.of(server.getConnection());
        테이블을_생성함(clazz);
    }

    @Nested
    @DisplayName("findAll()")
    class findAll {

        @Test
        @DisplayName("저장된 모든 데이터를 조회함")
        void success() {
            //given
            Class<SelectPerson> clazz = SelectPerson.class;
            final long maxCount = 10;

            for (long i = 0; i < maxCount; i++) {
                SelectPerson selectPerson = new SelectPerson(i, "z", 3, "z", 1);
                데이터를_저장함(selectPerson);
            }

            //when
            List<SelectPerson> result = entityManager.findAll(clazz);

            //then
            assertThat(result).size().isEqualTo(maxCount);
        }

        @Test
        @DisplayName("저장된 데이터가 없을 경우 조회 건수가 0건")
        void resultSizeZero() {
            //when
            List<SelectPerson> result = entityManager.findAll(clazz);

            //then
            assertThat(result).size().isZero();
        }

        @Test
        @DisplayName("생성되지 않은 테이블에서 조회 요청할 경우 오류 출력")
        void notFoundTable() {
            //given
            Class<Person> personClass = Person.class;

            //when & then
            assertThrows(RuntimeException.class, () -> entityManager.findAll(personClass));
        }
    }

    @Nested
    @DisplayName("find()")
    class find {

        @Test
        @DisplayName("정상적으로 데이터 단건을 조회해 옴")
        void success() {
            //given
            final Long id = 1L;
            final String name = "name";
            final int age = 3;
            final String email = "z";
            final Integer index = 3;

            SelectPerson requst = new SelectPerson(id, name, age, email, index);
            데이터를_저장함(requst);

            //when
            SelectPerson result = entityManager.find(clazz, id);

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
        @DisplayName("없는 데이터 조회할 때 오류 출력")
        void notFound() {
            //given
            final Long id = -999L;

            //when & then
            assertThrows(RuntimeException.class, () -> entityManager.find(clazz, id));
        }

        @Test
        @DisplayName("없는 테이블에 데이터 조회할 때 오류 출력")
        void notFoundTable() {
            //given
            final Long id = -99L;

            //when & theen
            assertThrows(RuntimeException.class, () -> entityManager.find(Person.class, id));
        }
    }

    @Nested
    @DisplayName("persist()")
    class persist {

        @Test
        @DisplayName("성공적으로 데이터를 저장함")
        void success() {
            //givne
            final Long id = 2L;
            final String name = "name";
            final int age = 3;
            final String email = "dd";
            final Integer index = 0;
            final SelectPerson request = new SelectPerson(id, name, age, email, index);

            //when
            entityManager.persist(request);

            SelectPerson result = 데이터를_조회함(clazz, id);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result.getId()).isEqualTo(id);
                softAssertions.assertThat(result.getId()).isEqualTo(id);
                softAssertions.assertThat(result.getName()).isEqualTo(name);
                softAssertions.assertThat(result.getEmail()).isEqualTo(email);
                softAssertions.assertThat(result.getAge()).isEqualTo(age);
                softAssertions.assertThat(result.getIndex()).isNull();
            });
        }

        @Test
        @DisplayName("존재하지 않는 테이블에 데이터 저장 시도시 오류")
        void notFoundTable() {
            //given
            final Person person = new Person(3L, "zz", 3, "xx", 3);

            //when & then
            assertThrows(RuntimeException.class, () -> entityManager.persist(person));
        }
    }

    @Nested
    @DisplayName("remove()")
    class remove {

        @Test
        @DisplayName("데이터를 정상적으로 삭제함")
        void success() {
            //given
            final Long id = 4L;
            final SelectPerson person = new SelectPerson(id, "zz", 3, "xx", 3);

            데이터를_저장함(person);

            Class<SelectPerson> clazz = SelectPerson.class;

            //when
            entityManager.remove(clazz, id);

            //then
            assertThrows(RuntimeException.class, () -> 데이터를_조회함(clazz, id));
        }

        @Test
        @DisplayName("존재하지 않는 테이블의 데이터 삭제 시도시 오류")
        void notFoundTable() {
            //given
            final Long id = 5L;
            final Person person = new Person(id, "zz", 3, "xx", 3);

            Class<Person> clazz = Person.class;

            //when & then
            assertThrows(RuntimeException.class, () -> entityManager.remove(clazz, id));
        }
    }

    @Nested
    @DisplayName("update()")
    class update {

        @Test
        @DisplayName("성공적으로 데이터를 수정한다")
        void success() {
            //given
            final Long id = 6L;
            final String name = "name";
            final int age = 20;
            final String email = "email";
            final Integer index = 3;
            final SelectPerson request = new SelectPerson(id, name, age, email, index);

            final String changeName = "홍길동";

            데이터를_저장함(request);
            SelectPerson before = 데이터를_조회함(clazz, id);

            //when
            entityManager.update(new SelectPerson(id, changeName, age, email, index), id);

            SelectPerson after = 데이터를_조회함(clazz, id);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(after.getId()).isEqualTo(before.getId());
                softAssertions.assertThat(after.getName()).isNotEqualTo(before.getName());
                softAssertions.assertThat(after.getAge()).isEqualTo(before.getAge());
                softAssertions.assertThat(after.getEmail()).isEqualTo(before.getEmail());
                softAssertions.assertThat(after.getIndex()).isNull();
            });
        }
    }

    @AfterEach
    void after() {
        테이블을_삭제함(clazz);
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    private <T> T 데이터를_조회함(Class<T> tClass, Object id) {
        return entityManager.find(tClass, id);
    }

    private <T> void 테이블을_생성함(Class<T> tClass) {
        final TableName tableName = TableName을_생성함(tClass);
        final Columns columns = Columns을_생성함(tClass);

        jdbcTemplate.execute(dmlQuery.create(tableName, columns));
    }

    private <T> void 테이블을_삭제함(Class<T> tClass) {
        jdbcTemplate.execute(dmlQuery.drop(TableName을_생성함(tClass)));
    }

    private <T> void 데이터를_저장함(T t) {
        entityManager.persist(t);
    }
}
