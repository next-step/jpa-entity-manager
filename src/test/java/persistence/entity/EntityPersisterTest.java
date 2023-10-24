package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.person.SelectPerson;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.ddl.QueryDdl;
import persistence.sql.dml.QueryDml;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityPersisterTest {

    private Class<SelectPerson> selectPerson = SelectPerson.class;

    private EntityPersister entityPersister;
    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;

    @BeforeEach
    void init() throws SQLException {
        server = new H2();

        jdbcTemplate = new JdbcTemplate(server.getConnection());

        entityPersister = new EntityPersister<>(jdbcTemplate, selectPerson);

        jdbcTemplate.execute(QueryDdl.create(selectPerson));
    }

    @Nested
    @DisplayName("데이터를 저장하는 insert()")
    class persist {

        @Test
        @DisplayName("정상적으로 데이터를 저장합니다")
        void success() {
            //given
            final Long id = 5L;
            final String name = "zz";
            final int age = 30;
            final String email = "zz";
            final Integer index = 1;

            SelectPerson request = new SelectPerson(id, name, age, email, index);

            //when
            entityPersister.insert(request);

            String selectQuery = 아이디로_데이터를_조회하는_쿼리_생성(id);
            SelectPerson result = jdbcTemplate.queryForObject(selectQuery, new ResultMapper<>(SelectPerson.class));

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
        @DisplayName("없는 테이블에 데이터를 isnert할 경우 오류 출력")
        void invalidTable() {
            //given
            Person request = new Person(33L, "zz", 30, "xx", 2);

            //when & then
            assertThrows(RuntimeException.class, () -> entityPersister.insert(request));
        }

        @Test
        @DisplayName("중복되는 데이터 저장 시도시 오류 출력")
        void duplicate() {
            //given
            final Long id = 5L;
            final String name = "zz";
            final int age = 30;
            final String email = "zz";
            final Integer index = 1;

            SelectPerson request = new SelectPerson(id, name, age, email, index);
            entityPersister.insert(request);

            //when & then
            assertThrows(RuntimeException.class, () -> entityPersister.insert(request));
        }
    }

    @Nested
    @DisplayName("데이터를 삭제하는 delete()")
    class remove {

        @Test
        @DisplayName("기본키를 조건으로 데이터 삭제")
        void success() {
            //given
            final Long id = 99L;
            final String name = "zz";
            final int age = 30;
            final String email = "zz";
            final Integer index = 1;

            SelectPerson request = new SelectPerson(id, name, age, email, index);
            데이터를_저장함(request);

            //when
            entityPersister.delete(id);

            String selectQuery = 아이디로_데이터를_조회하는_쿼리_생성(id);

            //then
            assertThrows(RuntimeException.class,
                    () -> jdbcTemplate.queryForObject(selectQuery, new ResultMapper<>(SelectPerson.class)));
        }
    }

    @Nested
    @DisplayName("데이터를 변경하는 update()")
    class update {

        @Test
        @DisplayName("성공적으로 데이터를 수정한다")
        void success() {
            //given
            final Long id = 99L;
            final String name = "zz";
            final int age = 30;
            final String email = "zz";
            final Integer index = 1;

            SelectPerson insertPerson = new SelectPerson(id, name, age, email, index);
            데이터를_저장함(insertPerson);

            final String updateName = "홍길동";
            SelectPerson request = new SelectPerson(id, updateName, age, email, index);

            //when
            boolean resultFlag = entityPersister.update(request, id);

            String selectQuery = 아이디로_데이터를_조회하는_쿼리_생성(id);
            SelectPerson result = jdbcTemplate.queryForObject(selectQuery, new ResultMapper<>(SelectPerson.class));

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(resultFlag).isTrue();
                softAssertions.assertThat(result.getId()).isEqualTo(id);
                softAssertions.assertThat(result.getName()).isNotEqualTo(name);
                softAssertions.assertThat(result.getName()).isEqualTo(updateName);
                softAssertions.assertThat(result.getAge()).isEqualTo(age);
                softAssertions.assertThat(result.getEmail()).isEqualTo(email);
                softAssertions.assertThat(result.getIndex()).isNull();
            });
        }

        @Test
        @DisplayName("없는 테이블에 데이터를 isnert할 경우 오류 출력")
        void invalidTable() {
            //given
            final Long id = 33L;
            Person request = new Person(id, "zz", 30, "xx", 2);

            //when
            boolean result = entityPersister.update(request, id);

            //then
            assertThat(result).isFalse();
        }
    }

    @AfterEach
    void after() {
        jdbcTemplate.execute(QueryDdl.drop(selectPerson));
    }

    private String 아이디로_데이터를_조회하는_쿼리_생성(Long id) {
        Class<SelectPerson> clazz = SelectPerson.class;

        final TableName tableName = TableName.of(clazz);
        final Columns columns = Columns.of(clazz.getDeclaredFields());

        return QueryDml.select("findById", tableName, columns, id);
    }

    private <T> void 데이터를_저장함(T t) {
        final TableName tableName = TableName.of(t.getClass());
        final Columns columns = Columns.of(t.getClass().getDeclaredFields());

        jdbcTemplate.execute(QueryDml.insert(tableName, columns, t));
    }
}
