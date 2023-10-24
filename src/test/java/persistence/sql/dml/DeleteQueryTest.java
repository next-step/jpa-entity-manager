package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.person.SelectPerson;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryTest {
    @Test
    @DisplayName("@Column 없는 @Id 값 조건으로 delete문 생성")
    void deleteQuery() {
        //given
        final Long id = 3L;
        final String expectedQuery = String.format("DELETE FROM users WHERE id = %s", id);

        Class<Person> clazz = Person.class;
        final TableName tableName = TableName.of(clazz);
        final Columns columns = Columns.of(clazz.getDeclaredFields());

        //when
        String query = DeleteQuery.create(tableName, columns, 3L);

        //then
        assertThat(query).isEqualTo(expectedQuery);
    }

    @Test
    @DisplayName("@Column 있는 @Id 값 조건으로 delete문 생성")
    void deleteQueryByColumn() {
        //given
        final Long id = 3L;
        final String expectedQuery = String.format("DELETE FROM selectPerson WHERE select_person_id = %s", id);

        SelectPerson person = new SelectPerson(3L, "zz", 30, "zz", 1);

        Class<SelectPerson> clazz = SelectPerson.class;
        final TableName tableName = TableName.of(clazz);
        final Columns columns = Columns.of(clazz.getDeclaredFields());

        //when
        String query = DeleteQuery.create(tableName, columns, 3L);

        //then
        assertThat(query).isEqualTo(expectedQuery);
    }
}