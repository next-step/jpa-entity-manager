package persistence.sql.ddl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;

import database.DatabaseServer;
import database.H2;
import domain.ExistTablePerson;
import domain.NonExistentTablePerson;
import domain.NotEntityPerson;
import domain.Person;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.Query;

class QueryDdlTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private final DmlQuery dmlQuery = DmlQuery.getInstance();
    @BeforeAll
    static void start() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @Nested
    @DisplayName("요구사항1")
    class one {

        @Test
        @DisplayName("@Entity가 설정되어 있지 않은 경우 Query를 생성하지 않음")
        void notEntity() {
            //given
            Class<NotEntityPerson> personClass = NotEntityPerson.class;

            //when & then
            assertThrows(NullPointerException.class, () -> dmlQuery.create(TableName을_생성함(personClass),
                Columns을_생성함(personClass)));
        }
    }

    @Nested
    @DisplayName("요구사항2")
    class two {

        @Test
        @DisplayName("정상적으로 Class 정보를 읽어 CREATE QUERY 생성하여 실행 성공")
        void success() {
            //given
            Class<NonExistentTablePerson> personClass = NonExistentTablePerson.class;
            final TableName tableName = TableName을_생성함(personClass);
            final Columns columns = Columns을_생성함(personClass);

            //when
            String query = dmlQuery.create(tableName, columns);

            //then
            assertDoesNotThrow(() -> jdbcTemplate.execute(query));
        }
    }

    @Nested
    @DisplayName("요구사항3")
    class three {

        @Test
        @DisplayName("정상적으로 Class 정보를 읽어 CREATE QUERY 생성하여 실행 성공")
        void success() {
            //given
            Class<ExistTablePerson> personClass = ExistTablePerson.class;
            final TableName tableName = TableName을_생성함(personClass);
            final Columns columns = Columns을_생성함(personClass);

            //when
            String query = dmlQuery.create(tableName, columns);

            //then
            assertDoesNotThrow(() -> jdbcTemplate.execute(query));
        }
    }

    @Nested
    @DisplayName("요구사항4")
    class four {

        @Test
        @DisplayName("정상적으로 Person 테이블 삭제")
        void success() {
            //given
            Class<Person> personClass = Person.class;
            createTable(personClass);

            //when
            String query = dmlQuery.drop(TableName을_생성함(personClass));

            //then
            assertDoesNotThrow(() -> jdbcTemplate.execute(query));
        }

        @Test
        @DisplayName("@Entity가 없는 class는 drop query 생성하지 않는다.")
        void notMakeDropQuery() {
            //given
            Class<NotEntityPerson> personClass = NotEntityPerson.class;

            //when & then
            assertThrows(NullPointerException.class, () -> dmlQuery.drop(TableName을_생성함(personClass)));
        }
    }

    @AfterAll
    static void stop() {
        server.stop();
    }

    private <T> void createTable(Class<T> tClass) {
        final TableName tableName = TableName을_생성함(tClass);
        final Columns columns = Columns을_생성함(tClass);

        jdbcTemplate.execute(dmlQuery.create(tableName, columns));
    }
}
