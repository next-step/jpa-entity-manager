package persistence.entity;


import domain.Person;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Types;

import static org.assertj.core.api.Assertions.assertThat;

class EntityIdMapperTest {

    @Test
    @DisplayName("EntityIdMapper 를 통해 insert 시 ResultSet 으로 부터 id 를 받아 entity 에 주입할 수 있다.")
    void mapIdTest() throws SQLException {
        final Person person = new Person("min", 30, "jongmin@gmail.com");
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addRow(1L);
        rs.next();

        final EntityIdMapper entityIdMapper = new EntityIdMapper(Person.class);
        entityIdMapper.mapId(person, rs);

        assertThat(person.getId()).isEqualTo(1L);
    }
}
