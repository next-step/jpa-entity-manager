package persistence.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

import database.DatabaseServer;
import database.H2;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import jdbc.JdbcTemplate;
import org.assertj.core.api.SoftAssertions;
import org.h2.jdbc.JdbcResultSet;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class EntityLoaderTest {

    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;
    private Dialect dialect;

    @BeforeEach
    void setUp() throws Exception {
        server = new H2();
        server.start();
        dialect = new FakeDialect();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(QueryGenerator.of(Person.class, dialect).create());
    }

    @Test
    @DisplayName("엔터티 로더를 통해 ResultSet을 엔터티로 변환한다.")
    void loader() {
        //given
        EntityMeta entityMeta = new EntityMeta(Person.class);
        EntityLoader entityLoader = new EntityLoader(entityMeta);
        QueryGenerator<Person> q = QueryGenerator.of(Person.class, dialect);
        Person person = new Person("이름", 19, "asd@gmail.com");

        //when
        jdbcTemplate.execute(q.insert(person));
        List<Person> personList = jdbcTemplate.query(q.select().findAllQuery(),
                resultSet -> entityLoader.resultSetToEntity(Person.class, resultSet));


        //then
        assertSoftly((it) -> {
            it.assertThat(personList).hasSize(1);
            it.assertThat(personList.get(0).getName()).isEqualTo(person.getName());
            it.assertThat(personList.get(0).getAge()).isEqualTo(person.getAge());
            it.assertThat(personList.get(0).getEmail()).isEqualTo(person.getEmail());
        });
    }

}
