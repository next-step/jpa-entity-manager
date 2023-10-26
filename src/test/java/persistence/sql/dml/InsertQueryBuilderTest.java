package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.fake.FakeDialect;
import persistence.fake.UpperStringDirect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.testFixtures.Person;

class InsertQueryBuilderTest {

    private Dialect dialect;

    @BeforeEach
    void setUp() {
        dialect = new FakeDialect();
    }

    @Test
    @DisplayName("insert 쿼리를 생성한다.")
    void insert() {
        //given
        EntityMeta entityMeta = new EntityMeta(Person.class);
        QueryGenerator query = QueryGenerator.of(entityMeta, dialect);
        Person person = new Person("name", 3, "kbh@gm.com");

        //when
        String sql = query.insert().build(person);

        //then
        assertThat(sql).isEqualTo("INSERT INTO users (nick_name, old, email) VALUES ('name', 3, 'kbh@gm.com')");
    }

    @Test
    @DisplayName("다른 방언으로 insert 쿼리를 생성한다.")
    void insertDirect() {
        //given
        EntityMeta entityMeta = new EntityMeta(Person.class);
        QueryGenerator query = QueryGenerator.of(entityMeta, new UpperStringDirect());
        Person person = new Person("name", 3, "kbh@gm.com");

        //when
        String sql = query.insert().build(person);

        //then
        assertThat(sql).isEqualTo("INSERT INTO USERS (nick_name, old, email) VALUES ('name', 3, 'kbh@gm.com')");
    }


}
