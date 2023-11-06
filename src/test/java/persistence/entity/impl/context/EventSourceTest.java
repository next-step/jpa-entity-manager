package persistence.entity.impl.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityEntry;
import persistence.entity.EventSource;
import persistence.entity.type.EntityStatus;
import persistence.sql.dialect.H2ColumnType;

@DisplayName("EventSource 테스트")
class EventSourceTest {

    private EventSource eventSource;

    @BeforeEach
    void setUp() {
        eventSource = new DefaultPersistenceContext(new H2ColumnType());
    }

    @Test
    @DisplayName("Entity를 MANAGED 상태로 설정할 수 있다.")
    void setEntityStatusManaged() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        eventSource.managed(entity);

        //then
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.MANAGED);
    }

    @Test
    @DisplayName("Entity를 GONE 상태로 설정할 수 있다.")
    void setEntityStatusGone() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        eventSource.gone(entity);

        //then
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.GONE);
    }

    @Test
    @DisplayName("Entity를 DELETED 상태로 설정할 수 있다.")
    void setEntityStatusDeleted() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        eventSource.deleted(entity);

        //then
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.DELETED);
    }

    @Test
    @DisplayName("Entity를 SAVING 상태로 설정할 수 있다.")
    void setEntityStatusSaving() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        eventSource.saving(entity);

        //then
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.SAVING);
    }

    @Test
    @DisplayName("Entity를 Loading 상태로 설정할 수 있다.")
    void setEntityStatusLoading() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        eventSource.loading(entity);

        //then
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.LOADING);
    }

    @Test
    @DisplayName("Entity를 READ_ONLY 상태로 설정할 수 있다.")
    void setEntityStatusReadOnly() {
        //given
        final EventSourceTestEntity entity = new EventSourceTestEntity(1L, "test");

        //when
        eventSource.readOnly(entity);

        //then
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        assertThat(entityEntry.getStatus()).isEqualTo(EntityStatus.READ_ONLY);
    }

    @Entity
    static class EventSourceTestEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public EventSourceTestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        protected EventSourceTestEntity() {
        }
    }
}