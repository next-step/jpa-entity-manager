package persistence.entity.impl;

import domain.Person;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.SelectQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


class EntityLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private EntityLoader<Person> entityLoader;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new StubJdbcTemplate();
        entityLoader = new EntityLoader<>(jdbcTemplate);
    }

    @Test
    void testLoad() {
        // Given
        Long userId = 1L;

        // When
        Person loadedUser = entityLoader.load(Person.class, userId);

        // Then
        Objects.requireNonNull(loadedUser);
    }

    @Test
    void testLoadAll() {
        // When
        List<Person> loadAll = entityLoader.loadAll(Person.class);

        // Then
        assertEquals(2, loadAll.size());
    }
}

class StubJdbcTemplate extends JdbcTemplate {

    public StubJdbcTemplate() {
        super(null);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        // 테스트에 필요한 stub 구현
        Person Person = domain.Person.of(1L, "myName",199, "myAddress@gmaill.com",1);
        return (T) Person;
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        // 테스트에 필요한 stub 구현
        List<Person> people = new ArrayList<>();
        people.add(Person.of(1L, "John", 100,"john@amzn.com",1));
        people.add(Person.of(2L, "Jane", 10,"JaneWhite@cj.com",2));
        return (List<T>) people;
    }
}

class StubSelectQueryBuilder extends SelectQueryBuilder {

    public StubSelectQueryBuilder(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public String findAll(Class<?> clazz) {
        // 테스트에 필요한 stub 구현
        return "SELECT * FROM users";
    }

    @Override
    public String findById(Class<?> entityClass, Object id) {
        return "SELECT * FROM users WHERE id = ?";
    }
}