package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.sql.meta.EntityKey;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultPersistenceContextTest {
    @Test
    @DisplayName("엔티티 저장소에 엔티티를 추가한다.")
    void addEntity() throws NoSuchFieldException, IllegalAccessException {
        // given
        final PersistenceContext persistenceContext = new DefaultPersistenceContext();
        final EntityWithId entity = new EntityWithId(1L, "Jaden", 30, "test@email.com");

        // when
        persistenceContext.addEntity(entity);

        // then
        final Object managedEntity = getManagedEntity(persistenceContext, entity.getClass(), entity.getId());
        assertThat(managedEntity).isEqualTo(entity);
    }

    @Test
    @DisplayName("엔티티 저장소에서 엔티티를 반환한다.")
    void getEntity() {
        // given
        final PersistenceContext persistenceContext = new DefaultPersistenceContext();
        final EntityWithId entity = new EntityWithId(1L, "Jaden", 30, "test@email.com");
        persistenceContext.addEntity(entity);

        // when
        final EntityWithId managedEntity = persistenceContext.getEntity(entity.getClass(), entity.getId());

        // then
        assertThat(managedEntity).isEqualTo(entity);
    }

    @Test
    @DisplayName("엔티티 저장소에서 엔티티를 제거한다.")
    void removeEntity() throws NoSuchFieldException, IllegalAccessException {
        // given
        final PersistenceContext persistenceContext = new DefaultPersistenceContext();
        final EntityWithId entity = new EntityWithId(1L, "Jaden", 30, "test@email.com");
        persistenceContext.addEntity(entity);

        // when
        persistenceContext.removeEntity(entity);

        // then
        final Object managedEntity = getManagedEntity(persistenceContext, entity.getClass(), entity.getId());
        final EntityStatus entityStatus = getEntityStatus(persistenceContext, entity);
        assertAll(
                () -> assertThat(managedEntity).isNull(),
                () -> assertThat(entityStatus).isEqualTo(EntityStatus.GONE)
        );
    }

    private Object getManagedEntity(PersistenceContext persistenceContext, Class<?> clazz, Object id)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = persistenceContext.getClass().getDeclaredField("entityRegistry");
        field.setAccessible(true);
        final Map<EntityKey, Object> entityRegistry = (Map<EntityKey, Object>) field.get(persistenceContext);
        return entityRegistry.get(new EntityKey(clazz, id));
    }

    private EntityStatus getEntityStatus(PersistenceContext persistenceContext, Object entity)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = persistenceContext.getClass().getDeclaredField("entityEntryRegistry");
        field.setAccessible(true);
        final Map<Object, EntityEntry> entityEntryRegistry = (Map<Object, EntityEntry>) field.get(persistenceContext);
        return entityEntryRegistry.get(entity).getEntityStatus();
    }
}
