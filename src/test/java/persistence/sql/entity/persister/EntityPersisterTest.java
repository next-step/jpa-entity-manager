package persistence.sql.entity.persister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;
import persistence.sql.db.H2Database;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.SelectQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;
import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.loader.EntityLoader;
import persistence.sql.entity.loader.EntityLoaderImpl;
import persistence.sql.entity.manager.EntityManagerImpl;
import persistence.sql.entity.loader.EntityLoaderMapper;
import persistence.sql.entity.manager.EntityManger;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterTest extends H2Database {

    private EntityPersister<Person> entityPersister;
    private EntityLoader<Person> entityLoader;
    private EntityManger<Person> entityManager;
    private Person person;
    private Person person2;


    @BeforeEach
    void setUp() {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(Person.class);
        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());

        this.entityPersister = new EntityPersisterImpl<>(jdbcTemplate,
                new InsertQueryBuilder(entityMappingTable.getTableName()),
                new UpdateQueryBuilder(entityMappingTable.getTableName()),
                new DeleteQueryBuilder(entityMappingTable.getTableName()));
        this.entityLoader = new EntityLoaderImpl<>(
                jdbcTemplate,
                new EntityLoaderMapper<>(Person.class),
                new SelectQueryBuilder(entityMappingTable.getTableName(), columnClause));

        this.entityManager = new EntityManagerImpl<>(
                entityLoader,
                entityPersister);

        this.person = new Person(1L, "박재성", 10, "jason");
        this.person2 = new Person(2L, "이동규", 20, "cu");

        entityPersister.deleteAll();
        entityPersister.insert(person);
    }

    @DisplayName("데이터가 수정이 잘되는지 확인한다.")
    @Test
    void updateTest() {
        Person newPerson = new Person(person.getId(), "이동규", 20, "cu");

        entityPersister.update(newPerson);

        final Person result = entityManager.find(Person.class, person.getId());

        assertThat(newPerson).isEqualTo(result);
    }

    @DisplayName("Person 데이터 추가가 된다.")
    @Test
    void insertTest() {
        entityPersister.insert(person2);

        final Person result = entityManager.find(Person.class, person2.getId());

        assertThat(person2).isEqualTo(result);
    }

    @DisplayName("Person id값 없는것도 저장이 된다.")
    @Test
    void isExistsInsert() {
        Person personWithNotExistId = new Person(null, "테스트", 123, "test@nextstep.com");

        Object key = entityPersister.insertWithPk(personWithNotExistId);

        assertThat(key).isEqualTo(2L);
    }

    @DisplayName("Person 데이터를 삭제한다.")
    @Test
    void deleteTest() {
        entityPersister.delete(person);

        final Person result = entityManager.find(Person.class, person.getId());

        assertThat(result).isNull();
    }

}
