package persistence.sql.column;

import jakarta.persistence.GenerationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.sql.dialect.MysqlDialect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IdColumnTest {

    @DisplayName("Entity의 id 생성 전략에 맞는 idGeneratedStrategy를 반환한다.")
    @Test
    void getIdGeneratedStrategy() {
        IdColumn idColumn = new IdColumn(Person.class.getDeclaredFields(), new MysqlDialect());

        assertThat(idColumn.getIdGeneratedStrategy().getGenerationType()).isEqualTo(GenerationType.IDENTITY);
    }

    @DisplayName("id column의 정의를 반환한다.")
    @Test
    void getDefinition() {

        IdColumn idColumn = new IdColumn(Person.class.getDeclaredFields(), new MysqlDialect());

        assertThat(idColumn.getDefinition()).isEqualTo("id bigint auto_increment primary key");
    }

    @DisplayName("id column의 변환된 컬럼의 이름을 반환한다.")
    @Test
    void getName() {

        IdColumn idColumn = new IdColumn(Person.class.getDeclaredFields(), new MysqlDialect());

        assertThat(idColumn.getName()).isEqualTo("id");
    }

    @DisplayName("id column의 필드 이름을 반환한다.")
    @Test
    void getFieldName() {

        IdColumn idColumn = new IdColumn(Person.class.getDeclaredFields(), new MysqlDialect());

        assertThat(idColumn.getFieldName()).isEqualTo("id");
    }
}
