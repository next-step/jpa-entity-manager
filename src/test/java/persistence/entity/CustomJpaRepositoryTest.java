package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;

import java.sql.SQLException;

class CustomJpaRepositoryTest extends DatabaseTest {
    private QueryBuilder queryBuilder;

    @BeforeEach
    public void beforeEach() throws SQLException {
        super.beforeEach();
        queryBuilder = new QueryBuilder(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), new H2InsertQueryBuilder(), jdbcTemplate);
    }

    @Test
    void save() throws IllegalAccessException {
        CustomJpaRepository<Person, Long> customJpaRepository = new CustomJpaRepository<>(new EntityManagerImpl(queryBuilder));
        Person person = new Person(1L, "slow", 20, "email@email.com", 1);
        customJpaRepository.save(person);

        person.changeEmail("emailNew@email.com");
        customJpaRepository.save(person);
    }
}
