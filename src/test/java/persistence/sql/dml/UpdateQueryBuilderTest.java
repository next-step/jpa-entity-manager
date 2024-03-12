package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.conditions.WhereRecord;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {
    private final Dialect DIALECT = new H2Dialect();

    @Test
    @DisplayName("Update query builder 동작 테스트")
    void update() {
        // given
        Person person = Person.of(1L, "crong", 35, "test@123.com");

        UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.builder()
                .dialect(DIALECT)
                .entity(person)
                .where(List.of(WhereRecord.of("id", "=", person.getId())))
                .build();

        // when
        String updatedQuery = updateQueryBuilder.generateQuery();

        // then
        assertThat(updatedQuery).isEqualTo("UPDATE users SET nick_name = 'crong', old = 35, email = 'test@123.com' WHERE id = 1");
    }
}