package persistence.sql.dml.h2;

import domain.Person;
import domain.PersonFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class H2WhereIdQueryTest {

    @Test
    @DisplayName("id 로 조건을 거는 WHERE 문을 만들어 낸다.")
    void whereId() {
        assertThat(
                H2WhereIdQuery.build(Person.class, 1)
        ).isEqualTo(" WHERE id = 1");
    }

    @Test
    @DisplayName("Entity 의 id를 조건을 거는 WHERE 문을 만들어 낸다.")
    void whereEntity() {
        assertThat(
                H2WhereIdQuery.build(PersonFixture.createPerson())
        ).isEqualTo(" WHERE id = 1");
    }
}
