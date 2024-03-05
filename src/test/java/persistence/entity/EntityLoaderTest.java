package persistence.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.H2DBTestSupport;
import persistence.Person;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.InsertQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityLoaderTest extends H2DBTestSupport {
    EntityLoader entityLoader = new EntityLoader(jdbcTemplate);

    @BeforeEach
    public void setUp() {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect(), Person.class);;
        jdbcTemplate.execute(createQueryBuilder.build());
    }

    @AfterEach
    public void cleanUp() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(Person.class);
        jdbcTemplate.execute(dropQueryBuilder.build());
    }

    @DisplayName("rowMapper 를 이용한 find 테스트")
    @Test
    void testFind() {
        Person person = new Person(null, "nick_name", 10, "email", null);
        jdbcTemplate.execute(new InsertQueryBuilder(Person.class).build(person));

        Person findPerson = entityLoader.find(Person.class, 1L);

        assertSoftly(softly -> {
            softly.assertThat(findPerson.getId()).isEqualTo(1L);
            softly.assertThat(findPerson.getName()).isEqualTo("nick_name");
        });
    }
}
