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
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityMangerImplTest extends H2DBTestSupport {
    private final EntityPersister entityPersister = new EntityPersister(new H2GeneratedIdObtainStrategy(), jdbcTemplate);
    private final EntityLoader entityLoader = new EntityLoader(jdbcTemplate);
    private final PersistenceContext persistenceContext = new PersistContextImpl();
    private final EntityManger entityManger =
            new EntityMangerImpl(entityPersister, entityLoader, persistenceContext);
    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(Person.class);


    @BeforeEach
    public void setUp() {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(new H2Dialect(), Person.class);;
        jdbcTemplate.execute(createQueryBuilder.build());
    }

    @AfterEach
    public void cleanUp() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(Person.class);;
        jdbcTemplate.execute(dropQueryBuilder.build());
    }

    @Test
    @DisplayName("요구사항1: find")
    void testFind() {
        Long id = 1L;
        Person person = new Person(null, "nick_name", 10, "test@test.com", null);
        jdbcTemplate.execute(insertQueryBuilder.build(person));

        Person findPerson = entityManger.find(Person.class, id);

        assertThat(findPerson).isNotNull();
    }

    @Test
    @DisplayName("find시 영속성 컨텍스트에 있으면 쿼리 하지 않는다.")
    void testFindFromPersistenceContext() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10, "test@test.com", null);
        persistenceContext.addEntity(id, person);

        Person findPerson = entityManger.find(Person.class, id);

        assertThat(findPerson).isNotNull();
    }


    @Test
    @DisplayName("요구사항2: persist insert 로 나가는 경우")
    void testPersistInsert() {
        Person person = new Person(null, "nick_name", 10, "df", null);

        Object saved = entityManger.persist(person);
        Person savedPerson = (Person) saved;

        assertThat(savedPerson.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("요구사항2: persist update 로 나가는 경우")
    void testPersistUpdate() {
        String newName = "new_nick_name";
        Person person = new Person(null, "nick_name", 10, "test@test.com", null);
        jdbcTemplate.execute(insertQueryBuilder.build(person));
        person.changeName(newName);

        Object saved = entityManger.persist(person);
        Person savedPerson = (Person) saved;

        assertThat(savedPerson.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("요구사항3: delete")
    void testDelete() {
        Person person = new Person(1L, "nick_name", 10, "df", null);

        entityManger.remove(person);

        assertThrows(Exception.class, () -> {
            jdbcTemplate.queryForObject("select * from users where id = 1", rs -> new Person());
        });
    }
}
