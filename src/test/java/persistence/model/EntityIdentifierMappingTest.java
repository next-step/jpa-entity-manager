package persistence.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV3;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class EntityIdentifierMappingTest {

    private final Class<PersonV3> clazz = PersonV3.class;
    private final Field idField = clazz.getDeclaredField("id");

    private final EntityIdentifierMapping identifierMapping = new EntityIdentifierMapping(PersonV3.class, idField.getName(), idField);

    EntityIdentifierMappingTest() throws NoSuchFieldException {
    }

    @DisplayName("엔티티의 식별키를 가져온다")
    @Test
    public void getIdentifier() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();

        // when
        final Object personIdentifier = identifierMapping.getIdentifier(person);

        // then
        assertThat(personIdentifier).isEqualTo(person.getId());
    }

    @DisplayName("엔티티에 식별키 값을 주입한다")
    @Test
    public void setIdentifier() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub(null);
        final long id = 1L;

        // when
        identifierMapping.setIdentifierValue(person, id);

        // then
        assertThat(person.getId()).isEqualTo(id);
    }
}
