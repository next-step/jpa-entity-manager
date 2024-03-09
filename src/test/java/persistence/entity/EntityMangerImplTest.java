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
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityMangerImplTest extends H2DBTestSupport {
    private final EntityPersister entityPersister = new EntityPersister(new H2GeneratedIdObtainStrategy(), jdbcTemplate);
    private final EntityLoader entityLoader = new EntityLoader(jdbcTemplate);
    private final PersistenceContext persistenceContext = new PersistenceContextImpl();
    private final EntityEntryContext entityEntryContext = new EntityEntryContext();
    private final EntityManger entityManger =
            new EntityMangerImpl(entityPersister, entityLoader, persistenceContext, entityEntryContext);
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
    @DisplayName("find 시 영속성 컨텍스트에 없으면 쿼리하고 컨텍스트에 저장한다.")
    void testFind() {
        Long id = 1L;
        Person person = new Person(null, "nick_name", 10, "test@test.com", null);
        jdbcTemplate.execute(insertQueryBuilder.build(person));

        Person findPerson = entityManger.find(Person.class, id);

        EntityKey entityKey = new EntityKey(Person.class, id);
        assertSoftly(softly -> {
            softly.assertThat(findPerson).isNotNull();
            softly.assertThat(persistenceContext.getEntity(entityKey)).isNotNull();
        });
    }

    @Test
    @DisplayName("find 시 영속성 컨텍스트에 있으면 쿼리 하지 않는다.")
    void testFindFromPersistenceContext() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10, "test@test.com", null);
        EntityKey entityKey = new EntityKey(Person.class, id);
        persistenceContext.addEntity(entityKey, person);

        Person findPerson = entityManger.find(Person.class, id);

        assertThat(findPerson).isNotNull();
    }


    @Test
    @DisplayName("persist db 저장 테스트")
    void testPersistInsert() {
        Person person = new Person(null, "nick_name", 10, "df", null);

        Object saved = entityManger.persist(person);
        Person savedPerson = (Person) saved;

        assertThat(savedPerson.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("persist시 persistenceContext에 저장된다.")
    void testSaveEntityIntoPersistenceContext() {
        Person person = new Person(null, "nick_name", 10, "df", null);

        Person saved = (Person) entityManger.persist(person);

        Person findEntity = (Person) persistenceContext.getEntity(EntityKey.fromEntity(saved));
        assertThat(findEntity.getId()).isEqualTo(person.getId());
    }

    @Test
    @DisplayName("persist 시 entiryEntryContext에 managed 상태로 저장된다.")
    void testManagedStatusEntryAddedAtPersist() {
        Person person = new Person(null, "nick_name", 10, "df", null);

        Person saved = (Person) entityManger.persist(person);

        EntityEntry entityEntry = entityEntryContext.getEntry(EntityKey.fromEntity(saved));
        assertThat(entityEntry.getStatus()).isEqualTo(Status.MANAGED);
    }

    @Test
    @DisplayName("persist 이미 관리하고 있을시 에러")
    void throwWhenEntityAlreadyExist() {
        Person person = new Person(null, "nick_name", 10, "df", null);
        Person saved = (Person) entityManger.persist(person);

        assertThrows(EntityAlreadyExistsException.class, () -> {
            entityManger.persist(saved);
        });
    }

    @Test
    @DisplayName("update db 저장 테스트")
    void testMerge() {
        String newName = "new_nick_name";
        Person person = new Person(null, "nick_name", 10, "test@test.com", null);
        entityManger.persist(person);
        person.changeName(newName);

        Object saved = entityManger.merge(person);
        Person savedPerson = (Person) saved;

        assertThat(savedPerson.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("merge 시 관리하고있지 않은 엔티티는 에러")
    void throwWhenEntityEntryNotExists() {
        Person person = new Person(null, "nick_name", 10, "test@test.com", null);

        assertThrows(EntityNotExistsException.class, () -> {
            entityManger.merge(person);
        });
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

    @Test
    @DisplayName("delete 영속성 컨텍스트에서 삭제한다.")
    void removeFromPersistenceContextWhenDelete() {
        Person person = new Person(null, "nick_name", 10, "df", null);
        Person saved = (Person) entityManger.persist(person);

        entityManger.remove(saved);

        assertThat(persistenceContext.getEntity(EntityKey.fromEntity(person))).isNull();
    }
}
