package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.InvalidEntityException;
import domain.NonExistentTablePerson;
import domain.NotEntityPerson;
import domain.SelectPerson;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;

class SelectQueryTest {

    private static Query query;

    @BeforeAll
    static void beforeAll() {
        query = Query.getInstance();
    }

    @Test
    @DisplayName("@Entity 없는 클래스 select quert 생성시 오류")
    void notEntity() {
        //given
        final Class<NotEntityPerson> aClass = NotEntityPerson.class;
        final String methodName = "findAll";

        //when & then
        assertThrows(InvalidEntityException.class
            , () -> query.select(methodName, TableName을_생성함(aClass), Columns을_생성함(aClass), null));
    }

    @Test
    @DisplayName("@Table name이 없을 경우 클래스 이름으로 select query 생성")
    void nonTableName() {
        //given
        final String expectedQuery = "SELECT id, nick_name, old, email FROM NonExistentTablePerson";

        final Class<NonExistentTablePerson> aClass = NonExistentTablePerson.class;
        final String methodName = "findAll";

        final TableName tableName = TableName을_생성함(aClass);
        final Columns columns = Columns을_생성함(aClass);

        //when
        String q = query.select(methodName, tableName, columns, null);

        //then
        assertThat(q).isEqualTo(expectedQuery);
    }

    @Test
    @DisplayName("전체 데이터 조회하는 select문 생성")
    void findAll() {
        //given
        final String expectedQuery = "SELECT id, nick_name, old, email FROM users";

        final Class<Person> aClass = Person.class;

        final TableName tableName = TableName을_생성함(aClass);
        final Columns columns = Columns을_생성함(aClass);

        //when
        String q = query.select(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, null);

        //then
        assertThat(q).isEqualTo(expectedQuery);
    }

    @Test
    @DisplayName("findById 쿼리를 성공적으로 생성")
    void findById() {
        //given
        String expectedQuery = "SELECT select_person_id, nick_name, old, email FROM selectPerson WHERE select_person_id = 1";

        Class<SelectPerson> clazz = SelectPerson.class;
        final TableName tableName = TableName을_생성함(clazz);
        final Columns columns = Columns을_생성함(clazz);

        //when
        String q = query.select("findById", tableName, columns, 1L);

        //then
        assertThat(q).isEqualTo(expectedQuery);
    }
}
