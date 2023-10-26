package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.InvalidEntityException;
import domain.NonExistentEntityPerson;
import domain.NonExistentTablePerson;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.MetaUtils;
import persistence.sql.common.meta.TableName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;
import static persistence.sql.common.meta.MetaUtils.Values을_생성함;

class InsertQueryTest {

    private static final Query query = Query.getInstance();

    @Test
    @DisplayName("Person 객체를 읽어 @Transient 필드 제외하고 성공적으로 insert query 생성")
    void success() {
        //given
        final String expectedQuery = "INSERT INTO users (id, nick_name, old, email) VALUES(1, 'name', 3, 'zz@cc.com')";
        final Person person = new Person(1L, "name", 3, "zz@cc.com", 1);

        final TableName tableName = TableName을_생성함(person);
        final Columns columns = Columns을_생성함(person);
        final Values values = Values을_생성함(person);

        //when
        String q = query.insert(tableName, columns, values);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(q).isEqualTo(expectedQuery);
            softAssertions.assertThat(q).isNotEqualToIgnoringCase("index");
        });

    }

    @Test
    @DisplayName("@Entity가 없는 객체의 경우 insert query 생성하지 않고 exception 발생")
    void isNotEntity() {
        //given
        final NonExistentEntityPerson person = new NonExistentEntityPerson(1L, "name", 3);

        //when & then
        assertThrows(InvalidEntityException.class
                , () -> query.insert(TableName을_생성함(person.getClass())
                        , Columns을_생성함(person.getClass().getDeclaredFields())
                        , Values을_생성함(person.getClass().getDeclaredFields())));
    }

    @Test
    @DisplayName("@Table이 없는 객체의 경우 클래스명을 기반으로 insert query 생성")
    void isNonTableName() {
        //given
        final String expectedQuery = "INSERT INTO NonExistentTablePerson (id, nick_name, old, email) VALUES(1, 'name', 3, 'zz@cc.com')";
        final NonExistentTablePerson person = new NonExistentTablePerson(1L, "name", 3, "zz@cc.com");

        final TableName tableName = MetaUtils.TableName을_생성함(person);
        final Columns columns = Columns을_생성함(person);
        final Values values = Values을_생성함(person);

        //when
        String q = query.insert(tableName, columns, values);

        //then
        assertThat(q).isEqualTo(expectedQuery);
    }
}
