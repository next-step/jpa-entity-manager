package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.InvalidEntityException;
import persistence.person.NonExistentTablePerson;
import persistence.person.NotEntityPerson;
import persistence.person.SelectPerson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateQueryTest {
    @Test
    @DisplayName("@Table name 및 @Column name대로 쿼리문 생성")
    void updateQuery() {
        //given
        String expectedResult = "UPDATE users SET nick_name = 'zz', old = 30, email = 'xx' WHERE id = 3";
        Person person = new Person(3L, "zz", 30, "xx", 2);

        //when
        String result = UpdateQuery.create(person, 3L);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("@Entity는 있고 @Table이 없는 경우 클래스명으로 쿼리문 생성")
    void nonExistentTablePerson() {
        //given
        String expectedResult = "UPDATE NonExistentTablePerson SET nick_name = 'zz', old = 30, email = 'xx' WHERE id = 3";
        NonExistentTablePerson person = new NonExistentTablePerson(3L, "zz", 30, "xx");

        //when
        String result = UpdateQuery.create(person, 3L);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("@Id의 경우 @Column name대로 쿼리문 생성")
    void updateQueryById() {
        //given
        String expectedResult = "UPDATE selectPerson SET nick_name = 'zz', old = 30, email = 'xx' WHERE select_person_id = 3";
        SelectPerson person = new SelectPerson(3L, "zz", 30, "xx", 2);

        //when
        String result = UpdateQuery.create(person, 3L);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("@Entity가 없을 경우 쿼리문 생성되지 않음")
    void notEntity() {
        //given
        NotEntityPerson person = new NotEntityPerson(3L, "zz", 30);

        //when & then
        assertThrows(InvalidEntityException.class, () -> UpdateQuery.create(person, 3L));
    }
}
