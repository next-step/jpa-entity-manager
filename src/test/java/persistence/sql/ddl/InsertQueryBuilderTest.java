package persistence.sql.ddl;

import domain.Person;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.h2.H2InsertQueryBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class InsertQueryBuilderTest {

    @Test
    void insert() {
        InsertQueryBuilder insertQueryBuilder = new H2InsertQueryBuilder();
        Person person = new Person("slow", 20, "email@email.com", 1);

        String actual = insertQueryBuilder.createInsertBuild(person);

        assertThat(actual).isEqualTo("insert into Person (nick_name,old,email) values ('slow','20','email@email.com')");
    }
}
