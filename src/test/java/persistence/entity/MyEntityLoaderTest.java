package persistence.entity;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.sql.Person;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.domain.dialect.H2Dialect;
import persistence.support.DatabaseSetup;

import static org.assertj.core.api.Assertions.assertThat;

@DatabaseSetup
class MyEntityLoaderTest {

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect());
        String createQuery = createQueryBuilder.build(Person.class);
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown(JdbcTemplate jdbcTemplate) {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(new H2Dialect());
        String dropQuery = dropQueryBuilder.build(Person.class);
        jdbcTemplate.execute(dropQuery);
    }

    @Test
    void find() {
        //given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        EntityLoader entityLoader = new MyEntityLoader(jdbcTemplate);
        String expectName = "ABC";
        entityManager.persist(new Person(1L, expectName, 10, "ABC@email.com", 10));

        //when
        Person actual = entityLoader.find(Person.class, 1L);

        //then
        assertThat(actual).extracting("name")
                .isEqualTo(expectName);
    }
}
