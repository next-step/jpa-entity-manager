package persistence.sql.ddl.query.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.LegacyPerson;
import domain.Person;
import persistence.sql.dialect.h2.H2ConstraintsMapper;
import persistence.sql.dialect.h2.H2TypeMapper;
import persistence.sql.entity.model.NormalDomainType;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnBuilderTest {

    private NormalDomainType nameColumn;
    private NormalDomainType emailColumn;

    @BeforeEach
    void setUp() throws Exception {

        LegacyPerson legacyPerson = new LegacyPerson(1L, "박재성", 12);
        Person person = new Person(2L, "김성주", 12, "jpa@nextstep.com");

        this.nameColumn = NormalDomainType.of(legacyPerson.getClass().getDeclaredField("name"), legacyPerson);
        this.emailColumn = NormalDomainType.of(person.getClass().getDeclaredField("email"), person);
    }

    @DisplayName("DB에 컬럼 저장할 쿼리문을 반환한다.")
    @Test
    void getColumnSql() {
        ColumnBuilder columnBuilder = new ColumnBuilder(nameColumn, H2TypeMapper.newInstance(), H2ConstraintsMapper.newInstance());

        assertThat(columnBuilder.build()).isEqualTo("name VARCHAR");
    }

    @DisplayName("Column에 있는 값으로 반환한다.")
    @Test
    void isColumnSql() {
        ColumnBuilder columnBuilder = new ColumnBuilder(emailColumn, H2TypeMapper.newInstance(), H2ConstraintsMapper.newInstance());

        assertThat(columnBuilder.build()).isEqualTo("email VARCHAR  NOT NULL");
    }
}
