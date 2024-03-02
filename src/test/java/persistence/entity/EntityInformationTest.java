package persistence.entity;

import org.junit.jupiter.api.Test;
import persistence.sql.domain.DatabaseTable;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntityInformationTest {

    private final EntityInformation entityInformation = new EntityInformation();

    @Test
    void should_inspect_id_exist() {
        assertAll(
                () -> assertThat(entityInformation.isNew(new Person("cs", 29, "katd216@gmail.com", 1))).isTrue(),
                () -> assertThat(entityInformation.isNew(new Person(1L, "cs", 29, "katd216@gmail.com", 1))).isFalse()
        );
    }

    @Test
    void should_set_entity_id() {
        Long id = 1L;
        Person person = new Person("cs", 29, "katd216@gmail.com", 1);

        entityInformation.setEntityId(person, id);
        String entityId = new DatabaseTable(person).getPrimaryColumn().getColumnValue();

        assertThat(entityId).isEqualTo(String.valueOf(id));
    }
}
