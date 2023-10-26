package persistence.entity;


import domain.Person;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Types;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityRowMapperTest {

    private EntityRowMapper<Person> entityRowMapper;

    @BeforeEach
    void setUp() {
        final Class<Person> clazz = Person.class;
        entityRowMapper = new EntityRowMapper<>(clazz);
    }

    @Test
    @DisplayName("EntityRowMapper 를 이용해 객체를 ResultSet 으로 부터 생성 할 수 있다.")
    void rowMappingTest() throws SQLException {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("nick_name", Types.VARCHAR, 255, 0);
        rs.addColumn("old", Types.INTEGER, 10, 0);
        rs.addColumn("email", Types.VARCHAR, 255, 0);
        rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
        rs.next();

        final Person person = entityRowMapper.mapRow(rs);

        assertSoftly(softly -> {
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("min");
            softly.assertThat(person.getAge()).isEqualTo(30);
            softly.assertThat(person.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }
}
