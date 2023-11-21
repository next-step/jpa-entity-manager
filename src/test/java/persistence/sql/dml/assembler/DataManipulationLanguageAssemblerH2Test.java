package persistence.sql.dml.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DataManipulationLanguageGenerator;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.GetFieldValue;
import persistence.sql.usecase.GetIdDatabaseField;
import persistence.sql.usecase.GetTableNameFromClass;

class DataManipulationLanguageAssemblerH2Test {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler = createDataManipulationLanguageAssembler();

    @Test
    @DisplayName("insert sql 생성")
    void generateInsertTest() {
        // given
        Person person = new Person("안녕하세요", 25, "tongnamuu@naver.com");

        // when
        String sql = dataManipulationLanguageAssembler.generateInsert(person);

        // then
        String expected = "insert into users (nick_name, old, email) values ('안녕하세요', 25, 'tongnamuu@naver.com');";
        assertThat(sql).isEqualTo(expected);
    }

    @Test
    @DisplayName("select sql 생성")
    void generateSelect() {
        // when
        String sql = dataManipulationLanguageAssembler.generateSelect(Person.class);

        // then
        String expected = "select * from users;";
        assertThat(sql).isEqualTo(expected);
    }

    @Test
    @DisplayName("select with where sql 생성")
    void generateSelectWithWhere() {
        // when
        String sql = dataManipulationLanguageAssembler.generateSelectWithWhere(Person.class, 1L);

        // then
        String expected = "select * from users where id = 1;";
        assertThat(sql).isEqualTo(expected);
    }

    @Test
    @DisplayName("delete sql 생성")
    void generateDeleteWithWhere() {
        // given
        Person person = new Person("tongnamuu", 11, "tongnamuu@naver.com");
        person.setId(2L);

        // when
        String sql = dataManipulationLanguageAssembler.generateDeleteWithWhere(person);

        // then
        String expected = "delete from users where id = 2;";
        assertThat(sql).isEqualTo(expected);
    }

    @Test
    @DisplayName("update sql 생성")
    void generateUpdateWithWhere() {
        // given
        Person person = new Person("tongnamuu", 11, "tongnamuu@naver.com");
        person.setId(2L);

        // when
        String sql = dataManipulationLanguageAssembler.generateUpdate(person);

        // then
        String expected = "update users set nick_name='tongnamuu', old=11, email='tongnamuu@naver.com' where id = 2;";
        assertThat(sql).isEqualTo(expected);
    }

    private DataManipulationLanguageAssembler createDataManipulationLanguageAssembler() {
        H2Dialect h2Dialect = new H2Dialect();
        GetTableNameFromClass getTableNameFromClass = new GetTableNameFromClass();
        GetFieldFromClass getFieldFromClass = new GetFieldFromClass();
        GetFieldValue getFieldValue = new GetFieldValue();
        GetIdDatabaseField getIdDatabaseField = new GetIdDatabaseField(getFieldFromClass);
        DataManipulationLanguageGenerator dataManipulationLanguageGenerator = new DataManipulationLanguageGenerator(
            getTableNameFromClass,
            getFieldFromClass,
            getFieldValue, getIdDatabaseField);
        return new DataManipulationLanguageAssembler(
            h2Dialect, dataManipulationLanguageGenerator
        );
    }
}
