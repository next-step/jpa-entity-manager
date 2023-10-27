package persistence.sql.common.meta;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.InvalidEntityException;
import domain.NonExistentTablePerson;
import domain.NotEntityPerson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;

class TableNameTest {

    @Test
    @DisplayName("@Entity, @Table 존재하여 별도 name을 가져옴")
    void validTableName() {
        //given
        Class<Person> clazz = Person.class;

        //when
        TableName result = TableName을_생성함(clazz);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(result.getValue()).isEqualTo("users");
            softAssertions.assertThat(result.getValue()).isNotEqualTo("Person");
        });
    }

    @Test
    @DisplayName("@Entity가 존재하지 않으면 오류 출력")
    void invalidEntity() {
        //given
        Class<NotEntityPerson> clazz = NotEntityPerson.class;

        //when & then
        assertThrows(InvalidEntityException.class, () -> TableName.of(clazz));
    }

    @Test
    @DisplayName("@Entity는 존재하지만 별도 이름 지정하지 않았을 경우 클래스 이름으로 생성")
    void notTableName() {
        //given
        Class<NonExistentTablePerson> clazz = NonExistentTablePerson.class;

        //when
        TableName result = TableName을_생성함(clazz);

        //then
        assertThat(result.getValue()).isEqualTo("NonExistentTablePerson");
    }
}
