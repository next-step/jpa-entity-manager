package persistence.entity;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.Person;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.domain.dialect.H2Dialect;
import persistence.support.DatabaseSetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DatabaseSetup
class MyEntityManagerTest {

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
    @DisplayName("find 메서드는 주어진 클래스와 id에 해당하는 엔티티를 반환한다")
    void find() {
        // given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Long id = 1L;
        Person expected = new Person(id, "John", 25, "qwer@asdf.com", 1);
        String insertQuery = new InsertQueryBuilder().build(expected);
        jdbcTemplate.execute(insertQuery);

        // when
        Person person = entityManager.find(Person.class, id);

        // then
        assertThat(person).isNotNull();
    }

    @Test
    @DisplayName("persist 메서드는 주어진 객체를 저장한다.")
    void persist() {
        // given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Long id = 1L;
        String expectedName = "John";
        Person expected = new Person(id, expectedName, 25, "qwer@asdf.com", 1);
        entityManager.persist(expected);

        // when
        Person person = entityManager.find(Person.class, id);

        // then
        assertThat(person).extracting("name")
                .isEqualTo(expectedName);
    }

    @Test
    @DisplayName("remove 메서드는 주어진 객체를 삭제한다.")
    void remove() {
        //given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Person expected = new Person(1L, "name", 25, "qwer@asdf.com", 1);
        entityManager.persist(expected);

        //when
        entityManager.remove(expected);

        //then
        assertThatThrownBy(() -> entityManager.find(Person.class, 1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("flush 메서드는 변경된 객체를 업데이트 한다.")
    void flush() {
        //given
        MyEntityManager entityManager = new MyEntityManager(jdbcTemplate);
        Person person = new Person(1L, "name", 25, "qwer@asdf.com", 1);
        entityManager.persist(person);
        String updatedName = "ABC";
        Person updated = new Person(1L, updatedName, 30, "asdf@asdf.com", 1);
        entityManager.merge(updated);

        //when & then
        entityManager.flush();
        Person result = entityManager.find(Person.class, 1L);
        assertThat(result).extracting("name").isEqualTo(updatedName);
    }
}
