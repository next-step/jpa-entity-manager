package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTest;
import persistence.sql.ddl.h2.H2DeleteQueryBuilder;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;
import persistence.sql.ddl.h2.H2SelectQueryBuilder;
import persistence.sql.ddl.h2.H2UpdateQueryBuilder;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomJpaRepositoryTest extends DatabaseTest {
    private QueryBuilder queryBuilder;

    @BeforeEach
    public void beforeEach() throws SQLException {
        super.beforeEach();
        queryBuilder = new QueryBuilder(new H2SelectQueryBuilder(), new H2DeleteQueryBuilder(), new H2InsertQueryBuilder(), new H2UpdateQueryBuilder(), jdbcTemplate);
    }

    @Test
    void save() throws IllegalAccessException {
        insertDb();
        CustomJpaRepository<Person, Long> customJpaRepository = new CustomJpaRepository<>(Person.class, new EntityManagerImpl(queryBuilder));

        // 1. 영속 컨텍스트 내에서 Entity 를 조회
        Person persistencePerson = customJpaRepository.findById(1L);
        persistencePerson.changeEmail("emailNew@email.com");

        customJpaRepository.save(persistencePerson);

        Person persistencePerson2 = customJpaRepository.findById(1L);

        assertEquals(persistencePerson2, persistencePerson);
    }
}
