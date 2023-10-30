package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dbms.Dialect;
import persistence.testutils.ReflectionTestSupport;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static persistence.entity.PersonFixtures.fixture;

class EntityPersisterTest {

    private FakeJdbcTemplate mockJdbcTemplate;
    private EntityPersister entityPersister;

    @BeforeEach
    void setUp() {
        mockJdbcTemplate = new FakeJdbcTemplate();
        entityPersister = new EntityPersister<>(Person.class, mockJdbcTemplate, Dialect.H2);
    }

    @Test
    void insert() {
        Person person = fixture(1L, "name1", 20, "email1");

        entityPersister.insert(person);

        assertThat(mockJdbcTemplate.latestExecutionSql)
                .isEqualTo("INSERT INTO USERS (ID, NICK_NAME, OLD, EMAIL) VALUES (1, 'name1', 20, 'email1');");
    }

    @Test
    void update() {
        Person person = fixture(1L, "name1", 20, "email1");

        entityPersister.update(person);
        assertThat(mockJdbcTemplate.latestExecutionSql)
                .isEqualTo("UPDATE USERS \n" +
                        "SET NICK_NAME = 'name1', OLD = 20, EMAIL = 'email1' \n" +
                        "WHERE id = 1;");
    }

    @DisplayName("update시 Entity의 id가 null이면 IllegalStateException을 던진다")
    @Test
    void update_throws() {
        Person idNullPerson = fixture(1L, "name1", 20, "email1");
        ReflectionTestSupport.setFieldValue(idNullPerson, "id", null);


        assertThatThrownBy(() -> entityPersister.update(idNullPerson))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("id column value is null, entity = Person");
        assertThat(mockJdbcTemplate.latestExecutionSql).isNull();
    }

    @Test
    void delete() {
        Person person = fixture(1L, "name1", 20, "email1");

        entityPersister.delete(person);

        assertThat(mockJdbcTemplate.latestExecutionSql)
                .isEqualTo("DELETE FROM USERS \n" +
                        "  WHERE id = 1;");
    }

    @DisplayName("delete시 Entity의 id가 null이면 IllegalStateException을 던진다")
    @Test
    void delete_throws() {
        Person idNullPerson = fixture(1L, "name1", 20, "email1");
        ReflectionTestSupport.setFieldValue(idNullPerson, "id", null);


        assertThatThrownBy(() -> entityPersister.delete(idNullPerson))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("id column value is null, entity = Person");
        assertThat(mockJdbcTemplate.latestExecutionSql).isNull();
    }

    static class FakeJdbcTemplate extends JdbcTemplate {
        String latestExecutionSql;

        public FakeJdbcTemplate() {
            super(null);
        }

        @Override
        public void execute(String sql) {
            this.latestExecutionSql = sql;
        }

        @Override
        public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
            return null;
        }

        @Override
        public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
            return null;
        }
    }
}
