package persistence.sql.util;

import domain.Person;
import domain.PersonFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnConditionsTest {
    private Person entity;

    @BeforeEach
    void setUp() {
        entity = PersonFixture.createPerson();
    }

    @Test
    @DisplayName("Upsert 를 위한 조건을 entity 로부터 추출한다.")
    void forUpsert() {
        assertThat(
                ColumnConditions.forUpsert(entity)
        ).isEqualTo("nick_name = '고정완', old = 30, email = 'ghojeong@email.com'");
    }

    @Test
    @DisplayName("식별을 위한 조건을 entity 로부터 추출한다.")
    void forId() {

        assertThat(
                ColumnConditions.forId(entity)
        ).isEqualTo("id = 1");
    }
}
