package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;

import static org.assertj.core.api.Assertions.*;

class UpdateQueryBuilderTest {
    @Test
    @DisplayName("update 쿼리를 생성한다.")
    void update() {
        // given
        final EntityWithId entity = new EntityWithId(1L, "Jackson", 20, "test2@email.com");
        final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

        // when
        final String query = updateQueryBuilder.update(entity);

        // then
        assertThat(query).isEqualTo("UPDATE users SET nick_name = 'Jackson', old = 20, email = 'test2@email.com' WHERE id = 1");
    }
}
