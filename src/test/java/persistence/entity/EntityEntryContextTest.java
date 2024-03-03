package persistence.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import persistence.entity.exception.ObjectNotFoundException;
import persistence.sql.entity.Person;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class EntityEntryContextTest {

    @Test
    void should_change_entity_status() {
        Person entity = new Person(1L, "cs", 29, "katd216@gmail.com", 1);
        EntityEntryContext context = new EntityEntryContext();
        EntityEntry entry = context.get(entity);

        assertAll(
                () -> {
                    entry.preLoad();
                    validateStatus(entity, context, EntityStatus.LOADING);
                    entry.postLoad();
                    validateStatus(entity, context, EntityStatus.MANAGED);
                },
                () -> {
                    entry.postRemove();
                    validateStatus(entity, context, EntityStatus.GONE);
                }
        );
    }

    private void validateStatus(Person entity, EntityEntryContext context, EntityStatus expectStatus) throws NoSuchFieldException, IllegalAccessException {
        Field field = SimpleEntityEntry.class.getDeclaredField("status");
        field.setAccessible(true);
        assertThat(field.get(context.get(entity))).isEqualTo(expectStatus);
    }

    @Test
    void gone_status_cannot_be_reloaded() {
        Person entity = new Person(1L, "cs", 29, "katd216@gmail.com", 1);
        EntityEntryContext context = new EntityEntryContext();
        EntityEntry entry = context.get(entity);

        entry.postRemove();

        assertThatThrownBy(entry::prePersist)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("deleted/gone status not permitted to update/loading/saving");
    }

}
