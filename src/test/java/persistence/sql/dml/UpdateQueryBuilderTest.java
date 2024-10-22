package persistence.sql.dml;

import jdbc.InstanceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;

import static org.assertj.core.api.Assertions.*;

class UpdateQueryBuilderTest {
    @Test
    @DisplayName("update 쿼리를 생성한다.")
    void update() {
        // given
        final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
        final EntityWithId entity = new EntityWithId(1L, "Jackson", 20, "test@email.com");
        final Object snapshot = new InstanceFactory<>(entity.getClass()).copy(entity);
        entity.setName("Yang");
        entity.setAge(35);
        entity.setEmail("test2@email.com");

        // when
        final String query = updateQueryBuilder.update(entity, snapshot);

        // then
        assertThat(query).isEqualTo("UPDATE users SET nick_name = 'Yang', old = 35, email = 'test2@email.com' WHERE id = 1");
    }

    @Test
    @DisplayName("변경된 필드가 없는 엔티티로 update 쿼리를 생성하면 예외를 발생한다.")
    void update_exception() {
        // given
        final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
        final EntityWithId entity = new EntityWithId(1L, "Jackson", 20, "test@email.com");
        final Object snapshot = new InstanceFactory<>(entity.getClass()).copy(entity);

        // when & then
        assertThatThrownBy(() -> updateQueryBuilder.update(entity, snapshot))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(UpdateQueryBuilder.NOT_CHANGED_MESSAGE);
    }
}
