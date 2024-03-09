package persistence.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.H2DBTestSupport;
import persistence.Person;
import persistence.PersonRowMapper;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.WhereBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.dml.BooleanExpression.eq;

class EntityPersisterTest extends H2DBTestSupport {
    private final EntityPersister entityPersister = new EntityPersisterImpl(new H2GeneratedIdObtainStrategy(), jdbcTemplate);

    @BeforeEach
    public void setUp() {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect(), Person.class);
        jdbcTemplate.execute(createQueryBuilder.build());
    }

    @AfterEach
    public void cleanUp() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(Person.class);
        jdbcTemplate.execute(dropQueryBuilder.build());
    }

    @DisplayName("insert 테스트")
    @Test
    void testInsert() {
        Person person = new Person(null, "nick_name", 10, "email", null);

        entityPersister.insert(person);

        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(eq("id", 1L));
        Person findPerson = jdbcTemplate.queryForObject("select * from users where id = 1", new PersonRowMapper());
        assertThat(findPerson).isNotNull();
    }

    @DisplayName("update 테스트")
    @Test
    void testUpdate() {
        final String newName = "new_nick_name";
        Person person = new Person(null, "nick_name", 10, "email", null);
        jdbcTemplate.execute(new InsertQueryBuilder(Person.class).build(person));
        person.setId(1L);

        person.changeName(newName);
        boolean ret = entityPersister.update(person);


        Person findPerson = jdbcTemplate.queryForObject("select * from users where id = 1", new PersonRowMapper());
        assertSoftly(softly -> {
            softly.assertThat(ret).isTrue();
            softly.assertThat(findPerson.getName()).isEqualTo(newName);
        });
    }

    @DisplayName("delete 테스트")
    @Test
    void testDelete() {
        final String newName = "new_nick_name";
        Person person = new Person(null, "nick_name", 10, "email", null);
        jdbcTemplate.execute(new InsertQueryBuilder(Person.class).build(person));
        person.setId(1L);

        entityPersister.delete(person);

        assertThrows(RuntimeException.class, () -> {
            jdbcTemplate.queryForObject("select * from users where id = 1", rs -> new Person());
        });
    }
}
