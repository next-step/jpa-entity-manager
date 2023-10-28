package persistence.entity;

import domain.Person;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import mock.MockDatabaseServer;
import mock.MockDmlGenerator;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.PersistenceException;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityLoaderTest {

    static class MockJdbcTemplate extends JdbcTemplate {
        private final SimpleResultSet rs;

        public MockJdbcTemplate(final SimpleResultSet rs) throws SQLException {
            super(new MockDatabaseServer().getConnection());
            this.rs = rs;
        }

        @Override
        public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
            try (rs) {
                final List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs));
                }
                return result;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @DisplayName("loadById 를 통해 객체를 조회할 수 있다.")
    void loadByIdTest() throws SQLException {
        final Class<Person> clazz = Person.class;
        final SimpleResultSet rs = createBaseResultSet();
        rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        final Optional<Person> result = entityLoader.loadById(1L);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotEmpty();
            final Person person = result.get();
            softly.assertThat(person.getId()).isEqualTo(1L);
            softly.assertThat(person.getName()).isEqualTo("min");
            softly.assertThat(person.getAge()).isEqualTo(30);
            softly.assertThat(person.getEmail()).isEqualTo("jongmin4943@gmail.com");
        });
    }

    @Test
    @DisplayName("loadById 를 통해 객체를 조회시 없는 Id 를 조회하면 Optional.Empty 가 반환된다.")
    void loadByIdEmptyTest() throws SQLException {
        final Class<Person> clazz = Person.class;
        final SimpleResultSet rs = createBaseResultSet();
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        final Optional<Person> result = entityLoader.loadById(Integer.MAX_VALUE);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("loadById 를 통해 객체를 조회시 row 가 2개 이상 반환되면 Exception 이 던져진다..")
    void loadByIdDuplicateTest() throws SQLException {
        final Class<Person> clazz = Person.class;
        final SimpleResultSet rs = createBaseResultSet();
        rs.addRow(1L, "min", 30, "jongmin4943@gmail.com");
        rs.addRow(1L, "test", 20, "test@test.com");
        final EntityLoader<Person> entityLoader = new EntityLoader<>(clazz, new MockDmlGenerator(), new MockJdbcTemplate(rs));

        assertThatThrownBy(() -> entityLoader.loadById(1L)).isInstanceOf(PersistenceException.class);
    }

    private SimpleResultSet createBaseResultSet() {
        final SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("id", Types.BIGINT, 10, 0);
        rs.addColumn("nick_name", Types.VARCHAR, 255, 0);
        rs.addColumn("old", Types.INTEGER, 10, 0);
        rs.addColumn("email", Types.VARCHAR, 255, 0);
        return rs;
    }
}
