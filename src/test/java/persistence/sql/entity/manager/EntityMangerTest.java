package persistence.sql.entity.manager;

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
import persistence.sql.entity.loader.EntityLoaderImpl;
import persistence.sql.entity.loader.EntityLoaderMapper;
import persistence.sql.entity.persister.EntityPersisterImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMangerTest extends H2Database {

    private EntityManger<Person> entityManger;

    private Person person;

    @BeforeEach
    void setUp() {
        final EntityMappingTable entityMappingTable = EntityMappingTable.from(Person.class);
        final ColumnClause columnClause = new ColumnClause(entityMappingTable.getDomainTypes().getColumnName());

        this.entityManger = new EntityManagerImpl<>(
                new EntityLoaderImpl<>(
                        jdbcTemplate,
                        new EntityLoaderMapper<>(Person.class),
                        new SelectQueryBuilder(entityMappingTable.getTableName(), columnClause)
                ),
                new EntityPersisterImpl<>(
                        jdbcTemplate,
                        new InsertQueryBuilder(entityMappingTable.getTableName()),
                        new UpdateQueryBuilder(entityMappingTable.getTableName()),
                        new DeleteQueryBuilder(entityMappingTable.getTableName())
                )
        );

        this.person = new Person(1L, "박재성", 10, "jason");

        entityManger.remove(person);
        entityManger.persist(person);
    }

    @DisplayName("디비를 조회하여, 한건의 결과를 반환한다.")
    @Test
    void findTest() {
        Person actual = entityManger.find(Person.class, 1L);

        assertThat(person).isEqualTo(actual);
    }

    @DisplayName("디비에 데이터가 저장이된다.")
    @Test
    void insertTest() {
        Person newPerson = new Person(2L, "이동규", 11, "cu");
        entityManger.persist(newPerson);

        Person findPerson = entityManger.find(Person.class, 2L);
        assertThat(findPerson).isEqualTo(newPerson);
    }

    @DisplayName("디비 데이터가 업데이트가 된다.")
    @Test
    void updateTest() {
        Person updatePerson = new Person(person.getId(), "이동규", 20, "cu");
        entityManger.persist(updatePerson);

        Person findPerson = entityManger.find(Person.class, person.getId());
        assertThat(findPerson).isEqualTo(updatePerson);
    }

    @DisplayName("디비에 데이터가 삭제가 된다.")
    @Test
    void deleteTest() {
        entityManger.remove(person);

        Optional<Person> optionalPerson = Optional.ofNullable(entityManger.find(Person.class, person.getId()));

        assertThat(optionalPerson.isPresent()).isFalse();
    }

}
