package persistence.jpa;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.MysqlDialect;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomJpaRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private TableColumn table;
    private Dialect dialect;
    private JpaRepository<Person, Long> jpaRepository;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityManager = new EntityManagerImpl(jdbcTemplate, new MysqlDialect());
        jpaRepository = new CustomJpaRepository<>(entityManager);
        Class<Person> personEntity = Person.class;
        table = new TableColumn(personEntity);
        dialect = new MysqlDialect();

        createTable(personEntity);
    }

    private void createTable(Class<Person> personEntity) {
        Columns columns = new Columns(personEntity.getDeclaredFields(), dialect);
        IdColumn idColumn = new IdColumn(personEntity.getDeclaredFields(), dialect);

        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(table, columns, idColumn);

        String createQuery = createQueryBuilder.build();
        jdbcTemplate.execute(createQuery);
    }

    @AfterEach
    void tearDown() {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(table);
        String dropQuery = dropQueryBuilder.build();
        jdbcTemplate.execute(dropQuery);
    }

    @DisplayName("새로운 엔티티를 저장한다")
    @Test
    void save() {
        // given
        //when
        String email = "hong@test.com";
        String name = "name";
        Person person = jpaRepository.save(new Person(name, email, 20));

        // then
        assertAll(
                () -> assertThat(person).isNotNull(),
                () -> assertThat(person.getId()).isEqualTo(1L),
                () -> assertThat(person.getName()).isEqualTo(name),
                () -> assertThat(person.getEmail()).isEqualTo(email)
        );
    }

    @Test
    @DisplayName("영속화된 엔티티의 경우 값을 저장하지 않고 업데이트한다.")
    void save_merge() {
        //given
        Person person = new Person(null, "jon", 20, "jhon@test.com", 30);
        jpaRepository.save(person);

        //when
        Person updatePerson = new Person(1L, "john", 25, "john@test.com", 10);
        jpaRepository.save(updatePerson);

        //then
        Person findPerson = entityManager.find(Person.class, 1L);
        assertThat(findPerson).isEqualTo(updatePerson);
    }
}
