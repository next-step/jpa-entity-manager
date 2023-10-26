package persistence.entity;

import domain.Person;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import mock.MockDatabaseServer;
import mock.MockDmlGenerator;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Types;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityLoaderTest {

    static class MockJdbcTemplate extends JdbcTemplate {
        public MockJdbcTemplate() throws SQLException {
            super(new MockDatabaseServer().getConnection());
        }

        @Override
        public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
            try (final SimpleResultSet rs = new SimpleResultSet()) {
                rs.addColumn("id", Types.BIGINT, 10, 0);
                rs.addColumn("nick_name", Types.VARCHAR, 255, 0);
                rs.addColumn("old", Types.INTEGER, 10, 0);
                rs.addColumn("email", Types.VARCHAR, 255, 0);
                rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
                if (rs.next()) {
                    return rowMapper.mapRow(rs);
                }
                throw new RuntimeException("Data not exist");
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @DisplayName("loadById 를 통해 객체를 조회할 수 있다.")
    void loadByIdTest() throws SQLException {
        final Class<Person> clazz = Person.class;
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate());

        final Person person = entityLoader.loadById(1L);

        assertSoftly(softly -> {
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("min");
            softly.assertThat(person.getAge()).isEqualTo(30);
            softly.assertThat(person.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }
}
