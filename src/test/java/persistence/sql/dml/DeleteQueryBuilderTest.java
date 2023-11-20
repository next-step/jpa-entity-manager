package persistence.sql.dml;

import entity.Person;
import fixture.PersonFixture;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteQueryBuilderTest {

    @Test
    public void deleteById() {
        //when
        String deleteById = DeleteQueryBuilder.getQuery(PersonFixture.changgunyee());

        //then
        assertThat(deleteById)
                .isEqualTo("DELETE FROM users WHERE id=1;");
    }
}