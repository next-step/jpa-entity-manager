package persistence.sql.column;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.type.NullableType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TableColumnTest {


    @DisplayName("Table에 이름이 있으면 해당 정보를 테이블의 이름으로 반환한다.")
    @Test
    void getName() {
        TableColumn tableColumn = new TableColumn(Person.class);

        assertThat(tableColumn.getName()).isEqualTo("users");
    }

    @DisplayName("Table에 이름이 없으면 클래스 이름을 테이블의 이름으로 반환한다.")
    @Test
    void getNameWhenTableAnnotationIsNotPresent() {
        TableColumn tableColumn = new TableColumn(Person2.class);

        assertThat(tableColumn.getName()).isEqualTo("person2");
    }

    @Entity
    private static class Person2 {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }

    @DisplayName("이름이 CamelCase로 되어있는 클래스 이름을 snake_case로 변경한다.")
    @Test
    void getTableNameWithSnakCase() {
        TableColumn tableColumn = new TableColumn(PersonCamelCase.class);

        assertThat(tableColumn.getName()).isEqualTo("person_camel_case");
    }

    @Entity
    private static class PersonCamelCase {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }
}
