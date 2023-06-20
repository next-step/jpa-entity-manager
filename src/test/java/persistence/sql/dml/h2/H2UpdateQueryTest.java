package persistence.sql.dml.h2;

import domain.PersonFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class H2UpdateQueryTest {

    @Test
    @DisplayName("Person Entity 를 위한 update 쿼리를 생성한다.")
    void build() {
        String expected = "UPDATE users"
                + " SET nick_name = '고정완', old = 30, email = 'ghojeong@email.com'"
                + " WHERE id = 1";
        assertThat(
                H2UpdateQuery.build(PersonFixture.createPerson())
        ).isEqualTo(expected);
    }
}
