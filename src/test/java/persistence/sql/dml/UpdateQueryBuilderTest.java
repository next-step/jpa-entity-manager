package persistence.sql.dml;

import org.junit.jupiter.api.Test;
import persistence.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UpdateQueryBuilderTest {
    UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(Person.class);

    @Test
    void testUpdateQueryBuilder() {
        Person person = new Person(1L, "test", 10, "test", null);
        String expected = String.format("update users set old = %d, nick_name = '%s', email = '%s' where id = %d",
                person.getAge(),
                person.getName(),
                person.getEmail(),
                person.getId()
        );
        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(BooleanExpression.eq("id", 1L));

        String query = updateQueryBuilder.toQuery(person, whereBuilder);

        assertThat(query).isEqualTo(expected);
    }

}
