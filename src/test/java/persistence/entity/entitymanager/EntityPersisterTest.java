package persistence.entity.entitymanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jdbc.FakeJdbcTemplate;
import persistence.entity.Person;
import persistence.sql.dbms.Dialect;
import persistence.testutils.ReflectionTestSupport;

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

        assertThat(mockJdbcTemplate.getLatestExecutionSqlResult())
                .isEqualTo("INSERT INTO USERS (ID, NICK_NAME, OLD, EMAIL) VALUES (1, 'name1', 20, 'email1');");
    }

    @Test
    void update() {
        Person person = fixture(1L, "name1", 20, "email1");

        entityPersister.update(person);
        assertThat(mockJdbcTemplate.getLatestExecutionSqlResult())
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
        assertThat(mockJdbcTemplate.getLatestExecutionSqlResult()).isNull();
    }

    @Test
    void delete() {
        Person person = fixture(1L, "name1", 20, "email1");

        entityPersister.delete(person);

        assertThat(mockJdbcTemplate.getLatestExecutionSqlResult())
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
        assertThat(mockJdbcTemplate.getLatestExecutionSqlResult()).isNull();
    }
}
