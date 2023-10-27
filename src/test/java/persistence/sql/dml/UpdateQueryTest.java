package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.InvalidEntityException;
import domain.NonExistentTablePerson;
import domain.NotEntityPerson;
import domain.SelectPerson;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;
import static persistence.sql.common.meta.MetaUtils.Values을_생성함;

class UpdateQueryTest {
    private final static Query query = Query.getInstance();

    @Test
    @DisplayName("@Table name 및 @Column name대로 쿼리문 생성")
    void updateQuery() {
        //given
        String expectedResult = "UPDATE users SET nick_name = 'zz', old = 30, email = 'xx' WHERE id = 3";
        Person person = new Person(3L, "zz", 30, "xx", 2);

        Class<Person> clazz = Person.class;
        final TableName tableName = TableName을_생성함(clazz);
        final Columns columns = Columns을_생성함(clazz);
        final Values values = Values을_생성함(person);

        //when
        String result = query.update(values, tableName, columns, 3L);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("@Entity는 있고 @Table이 없는 경우 클래스명으로 쿼리문 생성")
    void nonExistentTablePerson() {
        //given
        String expectedResult = "UPDATE NonExistentTablePerson SET nick_name = 'zz', old = 30, email = 'xx' WHERE id = 3";
        NonExistentTablePerson person = new NonExistentTablePerson(3L, "zz", 30, "xx");

        Class<NonExistentTablePerson> clazz = NonExistentTablePerson.class;
        final TableName tableName = TableName을_생성함(clazz);
        final Columns columns = Columns을_생성함(clazz);
        final Values values = Values을_생성함(person);

        //when
        String result = query.update(values, tableName, columns, 3L);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("@Id의 경우 @Column name대로 쿼리문 생성")
    void updateQueryById() {
        //given
        String expectedResult = "UPDATE selectPerson SET nick_name = 'zz', old = 30, email = 'xx' WHERE select_person_id = 3";
        SelectPerson person = new SelectPerson(3L, "zz", 30, "xx", 2);

        Class<SelectPerson> clazz = SelectPerson.class;
        final TableName tableName = TableName을_생성함(clazz);
        final Columns columns = Columns을_생성함(clazz);
        final Values values = Values을_생성함(person);

        //when
        String result = query.update(values, tableName, columns, 3L);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("@Entity가 없을 경우 쿼리문 생성되지 않음")
    void notEntity() {
        //given
        NotEntityPerson person = new NotEntityPerson(3L, "zz", 30);

        Class<NotEntityPerson> clazz = NotEntityPerson.class;

        //when & then
        assertThrows(InvalidEntityException.class
                , () -> query.update(Values을_생성함(person), TableName을_생성함(clazz), Columns을_생성함(clazz), 3L));
    }
}
